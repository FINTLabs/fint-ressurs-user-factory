package no.fintlabs;

import no.fint.model.resource.administrasjon.personal.PersonalressursResource;
import no.fint.model.resource.felles.PersonResource;
import no.fintlabs.cache.FintCache;
import no.fintlabs.cache.FintCacheManager;
import no.fintlabs.user.User;
import no.fintlabs.user.UserEventListenerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;

@Configuration
public class ResourceEntityCacheConfiguration {

    private final FintCacheManager fintCacheManager;
    private final UserEventListenerService userEventListenerService;

    public ResourceEntityCacheConfiguration(FintCacheManager fintCacheManager, UserEventListenerService userEventListenerService) {
        this.fintCacheManager = fintCacheManager;
        this.userEventListenerService = userEventListenerService;
    }

    @Bean
    FintCache<String, PersonalressursResource> personalressursResourceCache() {
        FintCache<String, PersonalressursResource> cache = createCache(PersonalressursResource.class);
        cache.addEventListener(userEventListenerService::onPersonalressursEvent);
        return cache;
    }

    @Bean
    FintCache<String, PersonResource> personResourceCache() {
        FintCache<String, PersonResource> cache = createCache(PersonResource.class);
        cache.addEventListener(userEventListenerService::onPersonEvent);
        return cache;
    }

    private <V> FintCache<String, V> createCache(Class<V> resourceClass) {
        return fintCacheManager.createCache(
                resourceClass.getName().toLowerCase(Locale.ROOT),
                String.class,
                resourceClass
        );
    }

}
