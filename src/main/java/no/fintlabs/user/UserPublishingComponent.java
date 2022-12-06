package no.fintlabs.user;

import lombok.extern.slf4j.Slf4j;
import no.fint.model.felles.kompleksedatatyper.Periode;
import no.fint.model.resource.administrasjon.organisasjon.OrganisasjonselementResource;
import no.fint.model.resource.administrasjon.personal.ArbeidsforholdResource;
import no.fint.model.resource.administrasjon.personal.PersonalressursResource;
import no.fint.model.resource.felles.PersonResource;
import no.fintlabs.cache.FintCache;
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
                .stream();
    }

    private Optional<OrganisasjonselementResource> getOrganisasjonsElementResource(String href, Date currentTime) {
        return organisasjonselementResourceCache
                .getOptional(href)
                .filter(organisasjonselementResource ->
                        isValid(organisasjonselementResource.getGyldighetsperiode(), currentTime));
    }

    private Optional<ArbeidsforholdResource> getArbeidsforholdResource(String href, Date currentTime) {
        return arbeidsforholdResourceCache
                .getOptional(href)
                .filter(arbeidsforholdResource ->
                                isValid(
                                        arbeidsforholdResource.getArbeidsforholdsperiode() != null
                                                ? arbeidsforholdResource.getArbeidsforholdsperiode()
                                                : arbeidsforholdResource.getGyldighetsperiode()
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
