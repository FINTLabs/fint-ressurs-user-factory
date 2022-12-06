package no.fintlabs.user;

import lombok.extern.slf4j.Slf4j;
import no.fint.model.felles.kompleksedatatyper.Kontaktinformasjon;
import no.fint.model.felles.kompleksedatatyper.Periode;
import no.fint.model.resource.administrasjon.organisasjon.OrganisasjonselementResource;
import no.fint.model.resource.administrasjon.personal.ArbeidsforholdResource;
import no.fint.model.resource.administrasjon.personal.PersonalressursResource;
import no.fint.model.resource.felles.PersonResource;
import no.fintlabs.cache.FintCache;
import no.fintlabs.links.ResourceLinkUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Component
public class UserPublishingComponent {

    private final FintCache<String, PersonResource> personResourceCache;
    private final FintCache<String, PersonalressursResource> personalressursResourceCache;
    private final FintCache<String, OrganisasjonselementResource> organisasjonselementResourceCache;
    private final FintCache<String, ArbeidsforholdResource> arbeidsforholdResourceCache;

    private final UserEntityProducerService userEntityProducerService;

    public UserPublishingComponent(
            FintCache<String, PersonResource> personResourceCache,
            FintCache<String, PersonalressursResource> personalressursResourceCache,
            FintCache<String, OrganisasjonselementResource> organisasjonselementResourceCache,
            FintCache<String, ArbeidsforholdResource> arbeidsforholdResourceCache
    ) {
        this.personResourceCache = personResourceCache;
        this.personalressursResourceCache = personalressursResourceCache;
        this.organisasjonselementResourceCache = organisasjonselementResourceCache;
        this.arbeidsforholdResourceCache = arbeidsforholdResourceCache;
    }

    @Scheduled(initialDelay = 5000L, fixedDelay = 20000L)
    public void doSomething() {
        Date currentTime = Date.from(Instant.now());
        personalressursResourceCache.getAllDistinct()
                .stream()
                .filter(personalressursResource -> isValid(personalressursResource.getAnsettelsesperiode(), currentTime))
                .filter(personalressursResource -> !personalressursResource.getArbeidsforhold().isEmpty())
                .map(personalressursResource -> createUser(personalressursResource, currentTime));
        // TODO: 06/12/2022 For each:
        //  check if already published on event topic (compare hash)
        //  publish if not already published
    }

    private Optional<User> createUser(PersonalressursResource personalressursResource, Date currentTime) {
        Optional<PersonResource> personResourceOptional = getPersonResource(personalressursResource);
        if (personResourceOptional.isEmpty()) {
            return Optional.empty();
        }

        Optional<ArbeidsforholdResource> arbeidsforholdResourceOptional = getArbeidsforholdResource(personalressursResource, currentTime);
        if (arbeidsforholdResourceOptional.isEmpty()) {
            return Optional.empty();
        }

        Optional<OrganisasjonselementResource> organisasjonselementResource = arbeidsforholdResourceOptional
                .flatMap(arbeidsforholdResource -> getOrganisasjonsElementResource(arbeidsforholdResource, currentTime));
        if (organisasjonselementResource.isEmpty()) {
            return Optional.empty();
        }

        // TODO: 06/12/2022 leder
        ResourceLinkUtil.getFirstLink(
                organisasjonselementResource.get()::getLeder,
                organisasjonselementResource.get(),
                "leder"
        );

        return Optional.of(createUser(
                personalressursResource,
                personResourceOptional.get(),

        ));
    }

    private User createUser(
            PersonalressursResource personalressursResource,
            PersonResource personResource,
            PersonalressursResource lederPersonalressursResource
    ) {
        String mobilePhone = Optional.ofNullable(personResource.getKontaktinformasjon())
                .map(Kontaktinformasjon::getMobiltelefonnummer)
                .orElse("");

        return User
                .builder()
                .resourceId(ResourceLinkUtil.getFirstSelfLink(personalressursResource))
                .firstName(personResource.getNavn().getFornavn())
                .lastName(personResource.getNavn().getEtternavn())
                .userType(String.valueOf(UserUtils.userType.EMPLOYEE))
                .mobilePhone(mobilePhone)
                .managerRef(ResourceLinkUtil.getFirstSelfLink(lederPersonalressursResource))
                .build();
    }

    private Optional<PersonResource> getPersonResource(PersonalressursResource personalressursResource) {
        return personResourceCache.getOptional(
                ResourceLinkUtil.getFirstLink(
                        personalressursResource::getPerson,
                        personalressursResource,
                        "Person"
                ));
    }

    private Optional<OrganisasjonselementResource> getOrganisasjonsElementResource(ArbeidsforholdResource arbeidsforholdResource, Date currentTime) {
        return organisasjonselementResourceCache
                .getOptional(
                        ResourceLinkUtil.getFirstLink(
                                arbeidsforholdResource::getArbeidssted,
                                arbeidsforholdResource,
                                "Arbeidssted"
                        )
                )
                .filter(organisasjonselementResource ->
                        isValid(organisasjonselementResource.getGyldighetsperiode(), currentTime));
    }

    private Optional<PersonalressursResource> getLeder(OrganisasjonselementResource organisasjonselementResource) {

    }

    private Optional<ArbeidsforholdResource> getArbeidsforholdResource(PersonalressursResource personalressursResource, Date currentTime) {
        return arbeidsforholdResourceCache
                .getOptional(
                        ResourceLinkUtil.getFirstLink(
                                personalressursResource::getArbeidsforhold,
                                personalressursResource,
                                "Arbeidsforhold"
                        )
                )
                .filter(arbeidsforholdResource ->
                        isValid(
                                arbeidsforholdResource.getArbeidsforholdsperiode() != null
                                        ? arbeidsforholdResource.getArbeidsforholdsperiode()
                                        : arbeidsforholdResource.getGyldighetsperiode(),
                                currentTime
                        )
                );
    }

    private boolean isValid(Periode gyldighetsperiode, Date currentTime) {
        if (gyldighetsperiode == null) {
            throw new NullPeriodeException();
        }
        return currentTime.after(gyldighetsperiode.getStart())
                && isEndValid(gyldighetsperiode.getSlutt(), currentTime);
    }

    private boolean isEndValid(Date end, Date currentTime) {
        return end == null || currentTime.before(end);
    }

    public static class NullPeriodeException extends RuntimeException {
    }
}
