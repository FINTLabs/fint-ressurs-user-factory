package no.fintlabs.user;

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

    private final UserService userService;


    public UserEventListenerService(
            FintCache<String, PersonalressursResource> personalressursResourceCache,
            FintCache<String, PersonResource> personResourceCache,
            UserService userService) {
        this.personalressursResourceCache = personalressursResourceCache;
        this.personResourceCache = personResourceCache;
        this.userService = userService;

        personResourceCache.addEventListener(new FintEhCacheEventListener<>() {

            //public void onEvent(FintCacheEvent<String, PersonResource> event) {
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
