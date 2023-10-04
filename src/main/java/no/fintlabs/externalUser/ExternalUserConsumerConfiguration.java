package no.fintlabs.externalUser;

import lombok.extern.slf4j.Slf4j;
import no.fintlabs.externalUser.ExternalUser;
import no.fintlabs.externalUser.ExternalUserEntityProducerService;
import no.fintlabs.kafka.entity.EntityConsumerFactoryService;
import no.fintlabs.kafka.entity.topic.EntityTopicNameParameters;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

@Configuration
@Slf4j
public class ExternalUserConsumerConfiguration {
    
    @Bean
    public ConcurrentMessageListenerContainer<String, ExternalUser> externalUserConsumer(
            ExternalUserEntityProducerService externalUserEntityProducerService,
            EntityConsumerFactoryService entityConsumerFactoryService
    ){
        EntityTopicNameParameters entityTopicNameParameters = EntityTopicNameParameters
                .builder()
                .resource("externaluser")
                .build();
        
        return entityConsumerFactoryService.createFactory(
                ExternalUser.class,
                (ConsumerRecord<String,ExternalUser> consumerRecord)
                -> externalUserEntityProducerService.publishExternalUsers(consumerRecord.value()))
                .createContainer(entityTopicNameParameters);
        
    }
    
    
}
