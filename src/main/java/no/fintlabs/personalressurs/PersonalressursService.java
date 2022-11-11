package no.fintlabs.personalressurs;

import lombok.extern.slf4j.Slf4j;
import no.fint.model.resource.Link;
import no.fint.model.resource.administrasjon.personal.PersonalressursResource;
import no.fint.model.resource.felles.PersonResource;
import no.fintlabs.person.PersonEntity;
import no.fintlabs.user.User;
import no.fintlabs.user.UserEntityProducerService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PersonalressursService {
    private final UserEntityProducerService userEntityProducerService;
    private final PersonEntity personEntity;



    public PersonalressursService( UserEntityProducerService userEntityProducerService
            , PersonEntity personEntity) {
        this.userEntityProducerService = userEntityProducerService;
        this.personEntity = personEntity;
    }

    public void process(PersonalressursResource personalressursResource) {

        Map<String, List<Link>> links = personalressursResource.getLinks();
        String personLink = String.valueOf(links.get("person").get(0));

        PersonResource personResource = personEntity.get(personLink);

        User user = User.builder()
                .userId(personalressursResource.getAnsattnummer().getIdentifikatorverdi())
                .firstName(personResource.getNavn().getFornavn())
                .lastName(personResource.getNavn().getEtternavn())
                .fintSelfLink(personLink)
                .build();

        userEntityProducerService.publish(user);

    }
}

