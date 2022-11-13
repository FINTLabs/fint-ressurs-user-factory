package no.fintlabs.user;

import no.fint.model.resource.administrasjon.personal.PersonalressursResource;
import no.fint.model.resource.felles.PersonResource;
import no.fintlabs.cache.FintCache;
import no.fintlabs.cache.FintCacheEvent;
import no.fintlabs.links.ResourceLinkUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
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