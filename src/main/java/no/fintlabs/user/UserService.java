package no.fintlabs.user;


import lombok.extern.slf4j.Slf4j;
import no.fint.model.resource.administrasjon.personal.PersonalressursResource;
import no.fint.model.resource.felles.PersonResource;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserEntityService {
    private final UserEntityProducerService userEntityProducerService;

    public UserEntityService(UserEntityProducerService userEntityProducerService) {

        this.userEntityProducerService = userEntityProducerService;
    }


    public void prepareForPublish(PersonResource personResource, PersonalressursResource personalressursResource) {
        User user = User.builder()
                .userId(personalressursResource.getAnsattnummer().getIdentifikatorverdi())
                .firstName(personResource.getNavn().getFornavn())
                .lastName(personResource.getNavn().getEtternavn())
                .fintSelfLink(personResource.getSelfLinks().get(0).getHref())
                .build();

        userEntityProducerService.publish(user);


    }

}
