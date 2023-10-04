package no.fintlabs.user;

import lombok.extern.slf4j.Slf4j;
import no.fint.model.felles.kompleksedatatyper.Kontaktinformasjon;
import no.fint.model.resource.administrasjon.organisasjon.OrganisasjonselementResource;
import no.fint.model.resource.administrasjon.personal.ArbeidsforholdResource;
import no.fint.model.resource.administrasjon.personal.PersonalressursResource;
import no.fint.model.resource.felles.PersonResource;
import no.fintlabs.azureUser.AzureUserService;
import no.fintlabs.resourceServices.ArbeidsforholdService;
import no.fintlabs.links.ResourceLinkUtil;
import no.fintlabs.resourceServices.PersonService;
import no.fintlabs.resourceServices.PersonalressursService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;

@Slf4j
@Component
public class UserPublishingComponent {

    private final PersonService personService;
    private final PersonalressursService personalressursService;
    private final AzureUserService azureUserService;
    private final ArbeidsforholdService arbeidsforholdService;

    private final UserEntityProducerService userEntityProducerService;

    public UserPublishingComponent(
            PersonService personService,
            PersonalressursService personalressursService,
            ArbeidsforholdService arbeidsforholdService,
            AzureUserService azureUserService,
            UserEntityProducerService userEntityProducerService
    ) {
        this.personService = personService;
        this.personalressursService = personalressursService;
        this.arbeidsforholdService = arbeidsforholdService;
        this.azureUserService = azureUserService;
        this.userEntityProducerService = userEntityProducerService;
    }

    @Scheduled(
            initialDelayString = "${fint.kontroll.user.publishing.initial-delay}",
            fixedDelayString = "${fint.kontroll.user.publishing.fixed-delay}"
    )
    public void publishUsers() {
        Date currentTime = Date.from(Instant.now());
                List<User> validUsers = personalressursService.getAllValid(currentTime)
                .stream()
                .map(personalressursResource -> createUser(personalressursResource, currentTime))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        List<User> publishedUsers = userEntityProducerService.publishChangedUsers(validUsers);

        log.info("Published {} of {} valid users", publishedUsers.size(), validUsers.size());
    }

    private Optional<User> createUser(PersonalressursResource personalressursResource, Date currentTime) {

        Optional<PersonResource> personResourceOptional = personService.getPerson(personalressursResource);
        if (personResourceOptional.isEmpty()) {
            return Optional.empty();
        }

        Optional<ArbeidsforholdResource> arbeidsforholdOptional =
                arbeidsforholdService.getArbeidsforhold(personalressursResource.getArbeidsforhold(), currentTime);
        if (arbeidsforholdOptional.isEmpty()) {
            return Optional.empty();
        }

        Optional<OrganisasjonselementResource> arbeidsstedOptional = arbeidsforholdOptional
                .flatMap(arbeidsforhold -> arbeidsforholdService.getArbeidssted(arbeidsforhold, currentTime));

        List<String> additionalArbeidssteder = new ArrayList<>();
        if (arbeidsstedOptional.isPresent()) {
            List<ArbeidsforholdResource> additionalArbeidsforhold =
                    arbeidsforholdService.getAllValidArbeidsforholdAsList(personalressursResource.getArbeidsforhold(),
                            currentTime);
            List<Optional<OrganisasjonselementResource>> additionalOrgUnits =
                    arbeidsforholdService.getAllArbeidssteder(additionalArbeidsforhold, currentTime);
            additionalArbeidssteder = additionalOrgUnits
                    .stream()
                    .map(orgUnit -> orgUnit.get().getOrganisasjonsId().getIdentifikatorverdi())
                    .toList();
        }

        Optional<String> lederPersonalressursLinkOptional = arbeidsstedOptional
                .flatMap(arbeidssted -> ResourceLinkUtil.getOptionalFirstLink(arbeidssted::getLeder));

        return Optional.of(
                createUser(
                        personalressursResource,
                        personResourceOptional.get(),
                        lederPersonalressursLinkOptional.isEmpty()? "": lederPersonalressursLinkOptional.get(),
                        arbeidsstedOptional.isPresent()? arbeidsstedOptional.get().getOrganisasjonsnavn():"",
                        arbeidsstedOptional.isPresent()?arbeidsstedOptional.get().getOrganisasjonsId().getIdentifikatorverdi() :"",
                        additionalArbeidssteder
                )
        );
    }

    private User createUser(
            PersonalressursResource personalressursResource,
            PersonResource personResource,
            String lederPersonalressursHref,
            String organisasjonsnavn,
            String organisasjonsId,
            List<String> additionalArbeidsteder
    ) {
        String hrefSelfLink = ResourceLinkUtil.getFirstSelfLink(personalressursResource);
        String resourceId = hrefSelfLink.substring(hrefSelfLink.lastIndexOf("/") +1);

        String mobilePhone = Optional.ofNullable(personResource.getKontaktinformasjon())
                .map(Kontaktinformasjon::getMobiltelefonnummer)
                .orElse("");

        Map<String,String> azureUserAttributes = azureUserService.getAzureUserAttributes(resourceId);

        return User
                .builder()
                .resourceId(resourceId)
                .firstName(personResource.getNavn().getFornavn())
                .lastName(personResource.getNavn().getEtternavn())
                .userType(String.valueOf(UserUtils.UserType.EMPLOYEE))
                .mainOrganisationUnitName(organisasjonsnavn)
                .mainOrganisationUnitId(organisasjonsId)
                .organisationUnitIds(additionalArbeidsteder)
                .mobilePhone(mobilePhone)
                .managerRef(lederPersonalressursHref)
                .identityProviderUserObjectId(UUID.fromString(azureUserAttributes.getOrDefault("identityProviderUserObjectId","0-0-0-0-0")))
                .email(azureUserAttributes.getOrDefault("email",""))
                .userName(azureUserAttributes.getOrDefault("userName",""))
                .build();
    }

}
