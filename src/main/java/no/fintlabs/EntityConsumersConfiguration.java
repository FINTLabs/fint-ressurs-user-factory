package no.fintlabs;

import no.fint.model.resource.FintLinks;
import no.fint.model.resource.administrasjon.organisasjon.OrganisasjonselementResource;
import no.fint.model.resource.administrasjon.personal.ArbeidsforholdResource;
import no.fint.model.resource.administrasjon.personal.PersonalressursResource;
import no.fint.model.resource.felles.PersonResource;
import no.fintlabs.cache.FintCache;
import no.fintlabs.kafka.entity.EntityConsumerFactoryService;
import no.fintlabs.kafka.entity.topic.EntityTopicNameParameters;
import no.fintlabs.links.ResourceLinkUtil;
import no.fintlabs.user.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

@Configuration
public class EntityConsumersConfiguration {

    private final EntityConsumerFactoryService entityConsumerFactoryService;

    public EntityConsumersConfiguration(EntityConsumerFactoryService entityConsumerFactoryService) {
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
                )
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

    @Bean
    ConcurrentMessageListenerContainer<String, OrganisasjonselementResource> organisasjonselementResourceEntityConsumer(
            FintCache<String, OrganisasjonselementResource> organisasjonselementResourceCache
    ) {
        return createCacheConsumer(
                "administrasjon.organisasjon.organisasjonselement",
                OrganisasjonselementResource.class,
                organisasjonselementResourceCache
        );
    }

    @Bean
    ConcurrentMessageListenerContainer<String, ArbeidsforholdResource> arbeidsforholdResourceEntityConsumer(
            FintCache<String, ArbeidsforholdResource> arbeidsforholdResourceCache
    ) {
        return createCacheConsumer(
                "administrasjon.personal.arbeidsforhold",
                ArbeidsforholdResource.class,
                arbeidsforholdResourceCache
        );
    }

    @Bean
    ConcurrentMessageListenerContainer<String, User> userEntityConsumer(
            FintCache<String, Integer> publishedUserHashCache
    ) {
        return entityConsumerFactoryService.createFactory(
                User.class,
                consumerRecord -> publishedUserHashCache.put(
                        consumerRecord.value().getResourceId(),
                        consumerRecord.value().hashCode()
                )
        ).createContainer(EntityTopicNameParameters.builder().resource("user").build());
    }

}
