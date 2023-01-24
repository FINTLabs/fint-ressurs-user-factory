package no.fintlabs.user;

import lombok.extern.slf4j.Slf4j;
import no.fint.model.felles.kompleksedatatyper.Kontaktinformasjon;
import no.fint.model.resource.felles.PersonResource;
import no.fint.model.resource.utdanning.elev.ElevResource;
import no.fintlabs.links.ResourceLinkUtil;
import no.fintlabs.resourceServices.ElevService;
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
    private final UserEntityProducerService userEntityProducerService;

    public UserPublishingElevComponent(
            ElevService elevService,
            PersonUtdanningService personUtdanningService,
            UserEntityProducerService userEntityProducerService
    ){
        this.elevService = elevService;
        this.personUtdanningService = personUtdanningService;
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

        return Optional.of(
                createUser(elevResource,personResourceOptional.get())
        );
    }

    private User createUser(
            ElevResource elevResource,
            PersonResource personResource
    ){
        String mobilePhone = Optional.ofNullable(personResource.getKontaktinformasjon())
                .map(Kontaktinformasjon::getMobiltelefonnummer)
                .orElse("");
        return User.builder()
                .resourceId(ResourceLinkUtil.getFirstSelfLink(elevResource))
                .firstName(personResource.getNavn().getFornavn())
                .lastName(personResource.getNavn().getEtternavn())
                .userType(String.valueOf(UserUtils.UserType.STUDENT))
                .mobilePhone(mobilePhone)
                .build();
    }
}
