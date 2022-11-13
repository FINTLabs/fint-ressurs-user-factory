package no.fintlabs.user;


import lombok.extern.slf4j.Slf4j;
import no.fint.model.resource.Link;
import no.fint.model.resource.administrasjon.personal.PersonalressursResource;
import no.fint.model.resource.felles.PersonResource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserService {
    private final UserEntityProducerService userEntityProducerService;

    public UserService(UserEntityProducerService userEntityProducerService) {

        this.userEntityProducerService = userEntityProducerService;
    }


    public void prepareForPublish(PersonResource personResource, PersonalressursResource personalressursResource) {
        User user = User.builder()
                .userId(personalressursResource.getAnsattnummer().getIdentifikatorverdi())
                .firstName(personResource.getNavn().getFornavn())
                .lastName(personResource.getNavn().getEtternavn())
                .userName(personalressursResource.getBrukernavn().getIdentifikatorverdi())
                .mobilePhone(personResource.getKontaktinformasjon().getMobiltelefonnummer())
                .email(personResource.getKontaktinformasjon().getEpostadresse())
                .managerRef(getManagerRef(personalressursResource))
                .build();

        userEntityProducerService.publish(user);


    }

    private String getManagerRef(PersonalressursResource personalressursResource) {
        List<Link> arbeidsforhold = personalressursResource.getArbeidsforhold();
        for (Link arb:arbeidsforhold) {


        }


        return "dummy";
    }

}



