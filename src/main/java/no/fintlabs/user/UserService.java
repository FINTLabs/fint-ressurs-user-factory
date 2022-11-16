package no.fintlabs.user;


import lombok.extern.slf4j.Slf4j;
import no.fint.model.resource.administrasjon.personal.ArbeidsforholdResource;
import no.fint.model.resource.administrasjon.personal.PersonalressursResource;
import no.fint.model.resource.felles.PersonResource;
import no.fintlabs.arbeidforhold.ArbeidsforholdService;
import no.fintlabs.arbeidssted.ArbeidsstedService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserEntityProducerService userEntityProducerService;
    private final ArbeidsforholdService arbeidsforholdService;
    private final ArbeidsstedService arbeidsstedService;

    public UserService(UserEntityProducerService userEntityProducerService,
                       ArbeidsforholdService arbeidsforholdService,
                       ArbeidsstedService arbeidsstedService) {

        this.userEntityProducerService = userEntityProducerService;
        this.arbeidsforholdService = arbeidsforholdService;
        this.arbeidsstedService = arbeidsstedService;
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
                .managerRef(getManagerRef(personalressursResource))
                .build();

        userEntityProducerService.publish(user);

    }

    private String getManagerRef(PersonalressursResource personalressursResource) {
        List<String> arbeidsforholdHrefs = (personalressursResource.getArbeidsforhold())
                .stream().map(a -> a.getHref())
                .collect(Collectors.toList());

        Optional<ArbeidsforholdResource> currentArbeidsforhold = arbeidsforholdService.getNewestArbeidsforhold(arbeidsforholdHrefs);
        String leder = arbeidsstedService.getLeder(currentArbeidsforhold);

        return leder;
    }

}



