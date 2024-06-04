package no.fintlabs.user;

import lombok.extern.slf4j.Slf4j;
import no.fint.model.felles.kompleksedatatyper.Periode;
import no.fint.model.resource.administrasjon.personal.PersonalressursResource;
import no.fint.model.resource.utdanning.elev.ElevforholdResource;
import no.fintlabs.cache.FintCache;
import no.fintlabs.resourceServices.GyldighetsperiodeService;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

@Component
@Slf4j
public class UserUtils {

    private static GyldighetsperiodeService gyldighetsperiodeService = new GyldighetsperiodeService();
    private static FintCache<String,User> publishUserCache;


    public UserUtils(
            GyldighetsperiodeService gyldighetsperiodeService,
            FintCache<String,User> publishUserCache) {
        UserUtils.gyldighetsperiodeService = gyldighetsperiodeService;
        UserUtils.publishUserCache = publishUserCache;

    }

    public static String getFINTAnsattStatus(PersonalressursResource personalressursResource, Date currentTime) {
        Periode gyldighetsPeriode = personalressursResource.getAnsettelsesperiode();

        return gyldighetsperiodeService.isValid(gyldighetsPeriode,currentTime)
                ?"ACTIV"
                :"DISABLED";
    }

    public static String getFINTElevStatus(ElevforholdResource elevforhold, Date currentTime) {
        String resoursID = elevforhold.getSystemId().getIdentifikatorverdi();
        Periode gyldighetsperiode = elevforhold.getGyldighetsperiode();
        String status = gyldighetsperiodeService.isValid(gyldighetsperiode,currentTime)
                ?"ACTIV"
                :"DISABLED";

        log.debug("Systemid: {} stop: {}Status: {}", resoursID, elevforhold.getGyldighetsperiode().getSlutt(), status);

        return status;
    }

    public static boolean isUserAlreadyOnKafka(String resourceId){
        Optional<User> userhash = publishUserCache.getOptional(resourceId);

        return userhash.isPresent();
    }

    public static User getUserFromKafka(String resourceId){
        Optional<User> userFromKafka = publishUserCache.getOptional(resourceId);
        log.info("userFromKafka: {}", userFromKafka);

        return userFromKafka.orElse(null);
    }


    public enum UserType {
        EMPLOYEESTAFF,
        EMPLOYEEFACULTY,
        STUDENT,
        AFFILIATE
    }

}

