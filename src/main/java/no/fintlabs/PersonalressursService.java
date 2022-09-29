package no.fintlabs;

import no.fint.model.resource.administrasjon.personal.PersonalressursResource;
import org.springframework.stereotype.Service;

@Service
public class PersonalressursService {

    private final NewPersonalressursEventProducerService newPersonalressursEventProducerService;

    public PersonalressursService(NewPersonalressursEventProducerService newPersonalressursEventProducerService) {
        this.newPersonalressursEventProducerService = newPersonalressursEventProducerService;
    }

    public void process(PersonalressursResource personalressursResource) {
        newPersonalressursEventProducerService.publish(personalressursResource);
    }
}
