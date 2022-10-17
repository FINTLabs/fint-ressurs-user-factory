package no.fintlabs;


import no.fint.model.resource.felles.PersonResource;
import no.fintlabs.kafka.entity.EntityConsumerFactoryService;
import no.fintlabs.kafka.entity.topic.EntityTopicNameParameters;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import java.util.function.Consumer;

@Configuration
public class PersonConsumerConfiguration {

    @Bean
    public ConcurrentMessageListenerContainer<String, PersonResource> personressursConsumer(
         PersonressursService personressursService,
         EntityConsumerFactoryService entityConsumerFactoryService
    ){
        EntityTopicNameParameters entityTopicNameParameters = EntityTopicNameParameters
                .builder()
                .resource("administrasjon.personal.person").build();

        ConcurrentMessageListenerContainer container = entityConsumerFactoryService.createFactory(
                PersonResource.class,
                (ConsumerRecord<String,PersonResource> consumerRecord)
                -> personressursService.process(consumerRecord.value()))
                .createContainer(entityTopicNameParameters);

        return container;
    }

}
