package no.fintlabs.person;


import no.fint.model.resource.felles.PersonResource;
import no.fintlabs.kafka.entity.EntityConsumerFactoryService;
import no.fintlabs.kafka.entity.topic.EntityTopicNameParameters;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

@Configuration
public class PersonConsumerConfiguration {

    @Bean
    public ConcurrentMessageListenerContainer<String, PersonResource> personressursConsumer(
         PersonService personService,
         EntityConsumerFactoryService entityConsumerFactoryService
    ){
        EntityTopicNameParameters entityTopicNameParameters = EntityTopicNameParameters
                .builder()
                .resource("administrasjon.personal.person").build();

        ConcurrentMessageListenerContainer container = entityConsumerFactoryService.createFactory(
                PersonResource.class,
                (ConsumerRecord<String,PersonResource> consumerRecord)
                -> personService.process(consumerRecord.value()))
                .createContainer(entityTopicNameParameters);

        return container;
    }

}
