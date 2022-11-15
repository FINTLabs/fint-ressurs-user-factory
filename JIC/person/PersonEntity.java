package no.fintlabs.person;

import lombok.extern.slf4j.Slf4j;
import no.fint.model.resource.felles.PersonResource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class PersonEntity {


    private final static PersonEntity instance = new PersonEntity();
    private final Map<String, PersonResource> personResourceMap = new HashMap<String,PersonResource>();
    private PersonEntity(){}

    public static PersonEntity getInstance(){
        return instance;
    }
    public void put(String key ,PersonResource personResource){
        personResourceMap.put(key,personResource);
    }

    public PersonResource get(String personLink) {
        return personResourceMap.get(personLink);
    }
}
