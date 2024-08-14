package no.fintlabs.user;

import lombok.extern.slf4j.Slf4j;
import no.fint.model.felles.kompleksedatatyper.Periode;
import no.fint.model.resource.administrasjon.personal.PersonalressursResource;
import no.fint.model.resource.utdanning.elev.ElevforholdResource;
import no.fintlabs.cache.FintCache;
import no.fintlabs.resourceServices.GyldighetsperiodeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

@Component
@Slf4j
public class UserUtils {

    private static int DAYS_BEFORE_START_EMPLOYEE;
    @Value("${fint.kontroll.user.days-before-start-employee}")
    private void daysBeforeStartEmployee(int daysBeforeStartEmployee) {
        UserUtils.DAYS_BEFORE_START_EMPLOYEE = daysBeforeStartEmployee;
    };

    private static int DAYS_BEFORE_START_STUDENT;
    @Value("${fint.kontroll.user.days-before-start-student}")
    private void setDaysBeforeStartStudent(int daysBeforeStartStudent) {
        UserUtils.DAYS_BEFORE_START_STUDENT = daysBeforeStartStudent;
    };


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

        return gyldighetsperiodeService.isValid(gyldighetsPeriode,currentTime, DAYS_BEFORE_START_EMPLOYEE)
                ?"ACTIVE"
                :"DISABLED";
    }

    public static String getFINTElevStatus(ElevforholdResource elevforhold, Date currentTime) {
        String resoursID = elevforhold.getSystemId().getIdentifikatorverdi();
        Periode gyldighetsperiode = elevforhold.getGyldighetsperiode();
        String status = gyldighetsperiodeService.isValid(gyldighetsperiode,currentTime, DAYS_BEFORE_START_STUDENT)
                ?"ACTIVE"
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

