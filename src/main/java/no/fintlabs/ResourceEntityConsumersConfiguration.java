package no.fintlabs;

import no.fint.model.resource.FintLinks;
import no.fint.model.resource.administrasjon.personal.PersonalressursResource;
import no.fint.model.resource.felles.PersonResource;
import no.fintlabs.cache.FintCache;
import no.fintlabs.kafka.entity.EntityConsumerFactoryService;
import no.fintlabs.kafka.entity.topic.EntityTopicNameParameters;
import no.fintlabs.links.ResourceLinkUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.CommonLoggingErrorHandler;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

@Configuration
public class ResourceEntityConsumersConfiguration {

    private final EntityConsumerFactoryService entityConsumerFactoryService;


    public ResourceEntityConsumersConfiguration(EntityConsumerFactoryService entityConsumerFactoryService) {
        this.entityConsumerFactoryService = entityConsumerFactoryService;
    }

    private <T extends FintLinks> ConcurrentMessageListenerContainer<String, T> createCacheConsumer(
            String resourceReference,
            Class<T> resourceClass,
            FintCache<String, T> cache
    ) {
        return entityConsumerFactoryService.createFactory(
                resourceClass,
                consumerRecord -> cache.put(
                        ResourceLinkUtil.getSelfLinks(consumerRecord.value()),
                        consumerRecord.value()
                ),
                new CommonLoggingErrorHandler()
        ).createContainer(EntityTopicNameParameters.builder().resource(resourceReference).build());
    }


    @Bean
    ConcurrentMessageListenerContainer<String, PersonalressursResource> personalressursResourceEntityConsumer(
            FintCache<String, PersonalressursResource> personalressursResourceCache
    ) {
        return createCacheConsumer(
                "administrasjon.personal.personalressurs",
                PersonalressursResource.class,
                personalressursResourceCache
        );
    }

    @Bean
    ConcurrentMessageListenerContainer<String, PersonResource> personResourceEntityConsumer(
            FintCache<String, PersonResource> personResourceCache
    ) {
        return createCacheConsumer(
                "administrasjon.personal.person",
                PersonResource.class,
                personResourceCache
        );
    }

}
