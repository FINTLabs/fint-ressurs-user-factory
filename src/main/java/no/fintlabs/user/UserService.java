package no.fintlabs.user;


import lombok.extern.slf4j.Slf4j;
import no.fint.model.resource.administrasjon.personal.ArbeidsforholdResource;
import no.fint.model.resource.administrasjon.personal.PersonalressursResource;
import no.fint.model.resource.felles.PersonResource;
import no.fintlabs.arbeidforhold.ArbeidsforholdService;
import no.fintlabs.arbeidssted.ArbeidsstedService;
import no.fintlabs.links.ResourceLinkUtil;
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
            mobilePhone = personResource.getKontaktinformasjon().getMobiltelefonnummer();
        } catch (NullPointerException e) {
            log.info("mobilePhone not found");
            mobilePhone = "";
        }


        User user = User.builder()
                //.resourceId(personalressursResource.getAnsattnummer().getIdentifikatorverdi())
                .resourceId(ResourceLinkUtil.getFirstSelfLink(personalressursResource))
                .firstName(personResource.getNavn().getFornavn())
                .lastName(personResource.getNavn().getEtternavn())
                .userType(String.valueOf(UserUtils.userType.EMPLOYEE))
                .mobilePhone(mobilePhone)
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



