package no.fintlabs.externalUser;

import lombok.extern.slf4j.Slf4j;
import no.fintlabs.cache.FintCache;
import no.fintlabs.kafka.entity.EntityProducer;
import no.fintlabs.kafka.entity.EntityProducerFactory;
import no.fintlabs.kafka.entity.EntityProducerRecord;
import no.fintlabs.kafka.entity.topic.EntityTopicNameParameters;
import no.fintlabs.kafka.entity.topic.EntityTopicService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ExternalUserEntityProducerService {
    private final FintCache<String,Integer> publishedExternalUserHashCache;
    private final EntityProducer<ExternalUser> entityProducer;
    private final EntityTopicNameParameters entityTopicNameParameters;

    public ExternalUserEntityProducerService(
            FintCache<String, Integer> publishedExternalUserHashCache,
            EntityTopicService entityTopicService,
            EntityProducerFactory entityProducerFactory
    ){
        this.publishedExternalUserHashCache = publishedExternalUserHashCache;
        entityProducer = entityProducerFactory.createProducer(ExternalUser.class);
        this.entityTopicNameParameters = EntityTopicNameParameters
                .builder()
                .resource("externaluser")
                .build();
        entityTopicService.ensureTopic(entityTopicNameParameters,0);
    }

    public List<ExternalUser> publishChangedExternalUsers(List<ExternalUser> externalUsers){
        return externalUsers
                .stream()
                .filter(externalUser -> publishedExternalUserHashCache
                        .getOptional(String.valueOf(externalUser.getIdentityProviderUserObjectId()))
                        .map(publishedExternalUserHashCache -> publishedExternalUserHashCache != externalUser.hashCode())
                        .orElse(true)
                )
                .peek(this::publishChangedExternalUser)
                .toList();
    }

    private void publishChangedExternalUser(ExternalUser externalUser) {
        String key = String.valueOf(externalUser.getIdentityProviderUserObjectId());
        entityProducer.send(
                EntityProducerRecord.<ExternalUser>builder()
                        .topicNameParameters(entityTopicNameParameters)
                        .key(key)
                        .value(externalUser)
                        .build()
        );

    }

    public void publishExternalUsers(ExternalUser externalUser) {
        String key = String.valueOf(externalUser.getIdentityProviderUserObjectId());
        entityProducer.send(
                EntityProducerRecord.<ExternalUser>builder()
                        .topicNameParameters(entityTopicNameParameters)
                        .key(key)
                        .value(externalUser)
                        .build()
        );
    }
}
