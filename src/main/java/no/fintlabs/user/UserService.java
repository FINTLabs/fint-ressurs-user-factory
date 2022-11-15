package no.fintlabs.user;


import io.netty.util.internal.ObjectPool;
import lombok.extern.slf4j.Slf4j;
import no.fint.model.resource.Link;
import no.fint.model.resource.administrasjon.personal.PersonalressursResource;
import no.fint.model.resource.felles.PersonResource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService {
    private final UserEntityProducerService userEntityProducerService;

    public UserService(UserEntityProducerService userEntityProducerService) {

        this.userEntityProducerService = userEntityProducerService;
    }


    public void prepareForPublish(PersonResource personResource, PersonalressursResource personalressursResource) {
        String userName;
        String mobilePhone;
        String email;
        try {
            userName = personalressursResource.getBrukernavn().getIdentifikatorverdi();
        } catch (NullPointerException e) {
            log.info("username not found");
            userName= "";
        }
        try {
            mobilePhone = personResource.getKontaktinformasjon().getMobiltelefonnummer();
        } catch (NullPointerException e) {
            log.info("mobilePhone not found");
            mobilePhone = "";
        }
        try {
            email = personResource.getKontaktinformasjon().getEpostadresse();
        } catch (NullPointerException e) {
            log.info("email not found");
            email = "";
        }

        User user = User.builder()
                .userId(personalressursResource.getAnsattnummer().getIdentifikatorverdi())
                .firstName(personResource.getNavn().getFornavn())
                .lastName(personResource.getNavn().getEtternavn())
                .userName(userName)
                .mobilePhone(mobilePhone)
                .email(email)
                //.managerRef(getManagerRef(personalressursResource))
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



