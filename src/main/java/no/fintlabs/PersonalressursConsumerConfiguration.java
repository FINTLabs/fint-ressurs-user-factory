package no.fintlabs;

import no.fint.model.resource.administrasjon.personal.PersonalressursResource;
import no.fintlabs.kafka.entity.EntityConsumerFactoryService;
import no.fintlabs.kafka.entity.topic.EntityTopicNameParameters;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

@Configuration
public class PersonalressursConsumerConfiguration {

    @Bean
    public ConcurrentMessageListenerContainer<String, PersonalressursResource> personalressursConsumer(
            PersonalressursService personalressursService,
            EntityConsumerFactoryService entityConsumerFactoryService
    ) {
        return entityConsumerFactoryService.createFactory(
                        PersonalressursResource.class,
                        (ConsumerRecord<String, PersonalressursResource> consumerRecord)
                                -> personalressursService.process(consumerRecord.value()))
                                .createContainer(EntityTopicNameParameters
                                    .builder()
                                    .resource("administrasjon.personal.personalressurs")
                                    .build()
                );
    }



}
