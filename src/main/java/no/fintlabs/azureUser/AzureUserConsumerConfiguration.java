package no.fintlabs.azureUser;

import no.fintlabs.kafka.entity.EntityConsumerFactoryService;
import no.fintlabs.kafka.entity.topic.EntityTopicNameParameters;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;


public class AzureUserConsumerConfiguration {

    @Bean
    public ConcurrentMessageListenerContainer<String, AzureUser> azureUserConsumer(
            AzureUserService azureUserService,
            EntityConsumerFactoryService entityConsumerFactoryService
    ){
        EntityTopicNameParameters entityTopicNameParameters = EntityTopicNameParameters
                .builder()
                .resource("azureuser")
                .build();

        return entityConsumerFactoryService.createFactory(
                AzureUser.class,
                (ConsumerRecord<String,AzureUser> consumerRecord)
                -> azureUserService.updateUserEntity(consumerRecord.value()))
                .createContainer(entityTopicNameParameters);
    }
}
