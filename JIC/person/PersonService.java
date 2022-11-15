package no.fintlabs.person;

import lombok.extern.slf4j.Slf4j;
import no.fint.model.resource.administrasjon.personal.PersonalressursResource;
import no.fint.model.resource.felles.PersonResource;
import no.fintlabs.cache.FintCache;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PersonService {
    private final PersonEntity personEntity;
    private final FintCache<String, PersonalressursResource> personalressursResourceFintCache;


    public PersonService(PersonEntity personEntity, FintCache<String, PersonalressursResource> personalressursResourceFintCache) {
        this.personEntity = personEntity;
        this.personalressursResourceFintCache = personalressursResourceFintCache;
    }

    public void process(PersonResource personResource) {
       personEntity.put(personResource.getSelfLinks().get(0).getHref(),personResource);

    }


}
