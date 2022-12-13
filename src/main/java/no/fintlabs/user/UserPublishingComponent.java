package no.fintlabs.user;

import lombok.extern.slf4j.Slf4j;
import no.fint.model.felles.kompleksedatatyper.Kontaktinformasjon;
import no.fint.model.resource.administrasjon.personal.ArbeidsforholdResource;
import no.fint.model.resource.administrasjon.personal.PersonalressursResource;
import no.fint.model.resource.felles.PersonResource;
import no.fintlabs.links.ResourceLinkUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
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

    @Scheduled(initialDelay = 5000L, fixedDelay = 20000L)
    public void doSomething() {
        Date currentTime = Date.from(Instant.now());
        personalressursService.getAllValid(currentTime)
                .stream()
                .map(personalressursResource -> createUser(personalressursResource, currentTime));
        // TODO: 06/12/2022
        //  Cache entities on topic, with key as key and hash of content as value
        //  For each:
        //      check if already published on event topic (compare hash)
        //      publish if not already published
        //  compare keys from current users to published users, delete diff
    }

    private Optional<User> createUser(PersonalressursResource personalressursResource, Date currentTime) {
        Optional<PersonResource> personResourceOptional = personService.getPerson(personalressursResource);
        if (personResourceOptional.isEmpty()) {
            return Optional.empty();
        }

        Optional<ArbeidsforholdResource> arbeidsforholdOptional =
                arbeidsforholdService.getMainArbeidsforhold(personalressursResource.getArbeidsforhold(), currentTime);
        if (arbeidsforholdOptional.isEmpty()) {
            return Optional.empty();
        }

        Optional<String> lederPersonalressursLinkOptional =
                arbeidsforholdService.getLederHref(arbeidsforholdOptional.get(), currentTime);

        return Optional.of(
                createUser(
                        personalressursResource,
                        personResourceOptional.get(),
                        lederPersonalressursLinkOptional.orElse(null)
                )
        );
    }

    private User createUser(
            PersonalressursResource personalressursResource,
            PersonResource personResource,
            String lederPersonalressursHref
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
                .mobilePhone(mobilePhone)
                .managerRef(lederPersonalressursHref)
                .build();
    }

}
