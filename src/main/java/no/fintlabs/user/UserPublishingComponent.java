package no.fintlabs.user;

import lombok.extern.slf4j.Slf4j;
import no.fint.model.felles.kompleksedatatyper.Kontaktinformasjon;
import no.fint.model.resource.administrasjon.organisasjon.OrganisasjonselementResource;
import no.fint.model.resource.administrasjon.personal.ArbeidsforholdResource;
import no.fint.model.resource.administrasjon.personal.PersonalressursResource;
import no.fint.model.resource.felles.PersonResource;
import no.fintlabs.resourceServices.ArbeidsforholdService;
import no.fintlabs.links.ResourceLinkUtil;
import no.fintlabs.resourceServices.PersonService;
import no.fintlabs.resourceServices.PersonalressursService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class UserPublishingComponent {

    private final PersonService personService;
    private final PersonalressursService personalressursService;
    private final ArbeidsforholdService arbeidsforholdService;

    private final UserEntityProducerService userEntityProducerService;

    public UserPublishingComponent(
            PersonService personService,
            PersonalressursService personalressursService,
            ArbeidsforholdService arbeidsforholdService,
            UserEntityProducerService userEntityProducerService
    ) {
        this.personService = personService;
        this.personalressursService = personalressursService;
        this.arbeidsforholdService = arbeidsforholdService;
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
        log.debug("Ids of published users: {}",
                publishedUsers.stream()
                        .map(User::getResourceId)
                        .map(href -> href.substring(href.lastIndexOf("/") + 1))
                        .toList()
        );
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
        if (arbeidsstedOptional.isEmpty()) {
            return Optional.empty();
        }

        Optional<String> lederPersonalressursLinkOptional = arbeidsstedOptional
                .flatMap(arbeidssted -> ResourceLinkUtil.getOptionalFirstLink(arbeidssted::getLeder));
        if (lederPersonalressursLinkOptional.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(
                createUser(
                        personalressursResource,
                        personResourceOptional.get(),
                        lederPersonalressursLinkOptional.get(),
                        arbeidsstedOptional.get().getOrganisasjonsnavn()
                )
        );
    }

    private User createUser(
            PersonalressursResource personalressursResource,
            PersonResource personResource,
            String lederPersonalressursHref,
            String organisasjonsnavn
    ) {
        String mobilePhone = Optional.ofNullable(personResource.getKontaktinformasjon())
                .map(Kontaktinformasjon::getMobiltelefonnummer)
                .orElse("");

        return User
                .builder()
                .resourceId(ResourceLinkUtil.getFirstSelfLink(personalressursResource))
                .firstName(personResource.getNavn().getFornavn())
                .lastName(personResource.getNavn().getEtternavn())
                .userType(String.valueOf(UserUtils.UserType.EMPLOYEE))
                .organisationUnitName(organisasjonsnavn)
                .mobilePhone(mobilePhone)
                .managerRef(lederPersonalressursHref)
                .build();
    }

}
