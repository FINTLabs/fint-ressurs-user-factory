package no.fintlabs.user;

import no.fint.model.resource.administrasjon.personal.PersonalressursResource;
import no.fint.model.resource.felles.PersonResource;
import no.fintlabs.cache.FintCache;
import no.fintlabs.cache.FintCacheEvent;
import no.fintlabs.links.ResourceLinkUtil;
import org.springframework.stereotype.Service;

@Service
public class UserEventListenerService {

    private final FintCache<String, PersonalressursResource> personalressursResourceCache;
    private final FintCache<String, PersonResource> personResourceCache;

    private final UserEntityService userEntityService;

    public UserEventListenerService(
            FintCache<String, PersonalressursResource> personalressursResourceCache,
            FintCache<String, PersonResource> personResourceCache,
            UserEntityService userEntityService) {
        this.personalressursResourceCache = personalressursResourceCache;
        this.personResourceCache = personResourceCache;
        this.userEntityService = userEntityService;
    }

    public void onPersonEvent(FintCacheEvent<String, PersonResource> cacheEvent) {
        PersonResource personResource = cacheEvent.getNewValue();
        String personalressursHref = ResourceLinkUtil.getFirstLink(
                personResource::getPersonalressurs,
                personResource,
                "Personalressurs"
        );
        personalressursResourceCache.getOptional(personalressursHref)
                .ifPresent(personalressursResource -> userEntityService.prepareForPublish(
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
                .ifPresent(personResource -> userEntityService.prepareForPublish(
                        personResource, personalressursResource
                ));
    }
}