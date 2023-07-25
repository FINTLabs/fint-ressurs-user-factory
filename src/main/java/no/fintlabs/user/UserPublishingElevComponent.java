package no.fintlabs.user;

import lombok.extern.slf4j.Slf4j;
import no.fint.model.felles.kompleksedatatyper.Kontaktinformasjon;
import no.fint.model.resource.administrasjon.organisasjon.OrganisasjonselementResource;
import no.fint.model.resource.felles.PersonResource;
import no.fint.model.resource.utdanning.elev.ElevResource;
import no.fint.model.resource.utdanning.elev.ElevforholdResource;
import no.fint.model.resource.utdanning.utdanningsprogram.SkoleResource;
import no.fintlabs.links.ResourceLinkUtil;
import no.fintlabs.resourceServices.ElevService;
import no.fintlabs.resourceServices.ElevforholdService;
import no.fintlabs.resourceServices.PersonUtdanningService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class UserPublishingElevComponent {
    private final ElevService elevService;
    private final PersonUtdanningService personUtdanningService;
    private final ElevforholdService elevforholdService;
    private final UserEntityProducerService userEntityProducerService;

    public UserPublishingElevComponent(
            ElevService elevService,
            PersonUtdanningService personUtdanningService,
            ElevforholdService elevforholdService, UserEntityProducerService userEntityProducerService){
        this.elevService = elevService;
        this.personUtdanningService = personUtdanningService;
        this.elevforholdService = elevforholdService;
        this.userEntityProducerService = userEntityProducerService;
    }

    @Scheduled(
            initialDelayString = "${fint.kontroll.user.publishing.initial-delay-elev}",
            fixedDelayString = "${fint.kontroll.user.publishing.fixed-delay}"
    )
    public void publishElevUsers(){
        Date currentTime = Date.from(Instant.now());

        List<User> validElevUsers = elevService.getAllValidElever(currentTime)
                .stream()
                .map(elevResource -> createUser(elevResource,currentTime))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        List<User> publishedElevUsers = userEntityProducerService.publishChangedUsers(validElevUsers);

        log.info("Published {} of {} valid users (students) ", publishedElevUsers.size(), validElevUsers.size());
        log.debug("Ids of published users (students) : {}",
                publishedElevUsers.stream()
                        .map(User::getResourceId)
                        .map(href -> href.substring(href.lastIndexOf("/") + 1))
                        .toList()
        );

    }

    private Optional<User> createUser(ElevResource elevResource, Date currentTime) {
        Optional<PersonResource> personResourceOptional = personUtdanningService
                .getPersonUtdanning(elevResource);
        if (personResourceOptional.isEmpty()){
            return Optional.empty();
        }

        Optional<ElevforholdResource> elevforholdOptional =
                elevforholdService.getElevforhold(elevResource.getElevforhold(),currentTime);
        if (elevforholdOptional.isEmpty()) {
            return Optional.empty();
        }

        Optional<SkoleResource> skoleOptional = elevforholdOptional
                .flatMap(elevforhold -> elevforholdService.getSkole(elevforhold,currentTime));
        if (skoleOptional.isEmpty()){
            return Optional.empty();
        }

        Optional<OrganisasjonselementResource> skoleOrgUnitOptional = skoleOptional
                .flatMap(skole -> elevforholdService.getSkoleOrgUnit(skole,currentTime));
        if (skoleOrgUnitOptional.isEmpty()){
            return Optional.empty();
        }

        return Optional.of(
                createUser(
                        elevResource,
                        personResourceOptional.get(),
                        skoleOrgUnitOptional.isPresent()?
                                skoleOrgUnitOptional.get().getOrganisasjonsnavn():"",
                        skoleOrgUnitOptional.isPresent()?
                                skoleOrgUnitOptional.get().getOrganisasjonsId().getIdentifikatorverdi():""
                )

        );
    }

    private User createUser(
            ElevResource elevResource,
            PersonResource personResource,
            String organisasjonsnavn,
            String organisasjonsId
    ){

        String hrefSelfLink = ResourceLinkUtil.getFirstSelfLink(elevResource);
        String resourceId = hrefSelfLink.substring(hrefSelfLink.lastIndexOf("/") + 1);

        String mobilePhone = Optional.ofNullable(personResource.getKontaktinformasjon())
                .map(Kontaktinformasjon::getMobiltelefonnummer)
                .orElse("");
        return User.builder()
                .resourceId(resourceId)
                .firstName(personResource.getNavn().getFornavn())
                .lastName(personResource.getNavn().getEtternavn())
                .userType(String.valueOf(UserUtils.UserType.STUDENT))
                .mainOrganisationUnitName(organisasjonsnavn)
                .mainOrganisationUnitId(organisasjonsId)
                .mobilePhone(mobilePhone)
                .build();
    }
}
