package no.fintlabs;

import no.fint.model.resource.administrasjon.personal.PersonalressursResource;
import no.fintlabs.kafka.event.EventProducer;
import no.fintlabs.kafka.event.EventProducerFactory;
import no.fintlabs.kafka.event.EventProducerRecord;
import no.fintlabs.kafka.event.topic.EventTopicNameParameters;
import org.springframework.stereotype.Service;

@Service
public class PersonalressursEventProducerService {

    private final EventProducer<PersonalressursResource> eventProducer;
    private final EventTopicNameParameters eventTopicNameParameters;

    public PersonalressursEventProducerService(
            EventProducerFactory eventProducerFactory
    ) {
        eventProducer = eventProducerFactory.createProducer(PersonalressursResource.class);
        eventTopicNameParameters = EventTopicNameParameters
                .builder()
                .eventName("new-personalressurs")
                .build();
    }

    public void publish(PersonalressursResource personalressursResource) {
        eventProducer.send(
                EventProducerRecord
                        .<PersonalressursResource>builder()
                        .topicNameParameters(eventTopicNameParameters)
                        .value(personalressursResource)
                        .build()
        );
    }

}
