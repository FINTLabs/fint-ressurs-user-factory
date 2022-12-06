package no.fintlabs.user;

import no.fint.model.resource.administrasjon.organisasjon.OrganisasjonselementResource;
import no.fint.model.resource.administrasjon.personal.ArbeidsforholdResource;
import no.fint.model.resource.administrasjon.personal.PersonalressursResource;
import no.fint.model.resource.felles.PersonResource;
import no.fintlabs.cache.FintCache;
import no.fintlabs.cache.FintCacheEvent;
import no.fintlabs.cache.ehcache.FintEhCacheEventListener;
import no.fintlabs.links.ResourceLinkUtil;
import org.springframework.stereotype.Service;

@Service("userEventListenerService")
public class UserEventListenerService {
    private final FintCache<String, PersonalressursResource> personalressursResourceCache;
    private final FintCache<String, PersonResource> personResourceCache;
    private final FintCache<String, ArbeidsforholdResource> arbeidsforholdResourceCache;
    private final FintCache<String, OrganisasjonselementResource> organisasjonselementResourceCache;

    private final UserService userService;


    public UserEventListenerService(
            FintCache<String, PersonalressursResource> personalressursResourceCache,
            FintCache<String, PersonResource> personResourceCache,
            FintCache<String, ArbeidsforholdResource> arbeidsforholdResourceCache,
            FintCache<String, OrganisasjonselementResource> organisasjonselementResourceCache,
            UserService userService) {
        this.personalressursResourceCache = personalressursResourceCache;
        this.personResourceCache = personResourceCache;
        this.arbeidsforholdResourceCache = arbeidsforholdResourceCache;
        this.organisasjonselementResourceCache = organisasjonselementResourceCache;
        this.userService = userService;

        personResourceCache.addEventListener(new FintEhCacheEventListener<>() {
            @Override
            public void onEvent(FintCacheEvent<String, PersonResource> event) {
                onPersonEvent(event);
            }
        });
        personalressursResourceCache.addEventListener(new FintEhCacheEventListener<>() {
            @Override
            public void onEvent(FintCacheEvent<String, PersonalressursResource> event) {
                onPersonalressursEvent(event);
            }
        });
        arbeidsforholdResourceCache.addEventListener(new FintEhCacheEventListener<>(){
            @Override
            public void onEvent(FintCacheEvent<String, ArbeidsforholdResource> event) {
                onArbeidsforholdResourceEvent(event);
            }
        });
        organisasjonselementResourceCache.addEventListener(new FintEhCacheEventListener<>(){

            @Override
            public void onEvent(FintCacheEvent<String, OrganisasjonselementResource> event) {

            }
        });
    }

    private void onOrganisasjonselementResourceEvent(FintCacheEvent<String,OrganisasjonselementResource> cacheEvent) {
        OrganisasjonselementResource organisasjonselementResource = cacheEvent.getNewValue();
        String organisasjonselementressursHref = ResourceLinkUtil.getFirstLink(
                organisasjonselementResource::getLeder,
                organisasjonselementResource,
                "leder"
        );
    }

    private void onArbeidsforholdResourceEvent(FintCacheEvent<String,ArbeidsforholdResource> cacheEvent) {
        ArbeidsforholdResource arbeidsforholdResource = cacheEvent.getNewValue();
        String arbeidsforholdressursHref = ResourceLinkUtil.getFirstLink(
                arbeidsforholdResource::getArbeidssted,
                arbeidsforholdResource,
                "arbeidssted"
        );
        arbeidsforholdResourceCache.getOptional(arbeidsforholdressursHref)
                .ifPresent();
    }


    public void onPersonEvent(FintCacheEvent<String, PersonResource> cacheEvent) {
        PersonResource personResource = cacheEvent.getNewValue();
        String personalressursHref = ResourceLinkUtil.getFirstLink(
                personResource::getPersonalressurs,
                personResource,
                "Personalressurs"
        );
        personalressursResourceCache.getOptional(personalressursHref)
                .ifPresent(personalressursResource -> userService.prepareForPublish(
                personResource, personalressursResource
        ));
    }

    public void onPersonalressursEvent(FintCacheEvent<String, PersonalressursResource> cacheEvent) {
        PersonalressursResource personalressursResource = cacheEvent.getNewValue();
        String personHref = ResourceLinkUtil.getFirstLink(
                personalressursResource::getPerson,
                personalressursResource,
                "Person"
        );
        personResourceCache.getOptional(personHref)
                .ifPresent(personResource -> userService.prepareForPublish(
                        personResource, personalressursResource
                ));
    }
}
