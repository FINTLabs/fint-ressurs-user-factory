package no.fintlabs.person;

import lombok.extern.slf4j.Slf4j;
import no.fint.model.resource.felles.PersonResource;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PersonressursService {
    private final PersonEntity personEntity;

    public PersonressursService(PersonEntity personEntity) {
        this.personEntity = personEntity;
    }

    public void process(PersonResource personResource) {
       personEntity.put(personResource.getSelfLinks().get(0).getHref(),personResource);

    }


}
