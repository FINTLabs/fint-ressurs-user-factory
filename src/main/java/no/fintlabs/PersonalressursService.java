package no.fintlabs;

import lombok.extern.slf4j.Slf4j;
import no.fint.model.resource.administrasjon.personal.PersonalressursResource;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PersonalressursService {

    private final PersonalressursEventProducerService personalressursEventProducerService;

    public PersonalressursService(PersonalressursEventProducerService personalressursEventProducerService) {
        this.personalressursEventProducerService = personalressursEventProducerService;
    }

    public void process(PersonalressursResource personalressursResource) {
        log.info(personalressursResource.getAnsattnummer().getIdentifikatorverdi());

        personalressursEventProducerService.publish(personalressursResource);
    }
}
