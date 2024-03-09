package no.fintlabs.user;

import lombok.extern.slf4j.Slf4j;
import no.fint.model.felles.kompleksedatatyper.Periode;
import no.fint.model.resource.administrasjon.personal.PersonalressursResource;
import no.fint.model.resource.utdanning.elev.ElevforholdResource;
import no.fintlabs.resourceServices.GyldighetsperiodeService;

import java.util.Date;
import java.util.Optional;
@Slf4j
public class UserUtils {
    private static final GyldighetsperiodeService gyldighetsperiodeService = new GyldighetsperiodeService();

    public UserUtils(GyldighetsperiodeService gyldighetsperiodeService) {

    }

    public static String getUserStatus(PersonalressursResource personalressursResource, Date currentTime) {
        Periode gyldighetsPeriode = personalressursResource.getAnsettelsesperiode();

        return gyldighetsperiodeService.isValid(gyldighetsPeriode,currentTime)
                ?"ACTIV"
                :"DISABLED";
    }

    public static String getUserElevStatus(ElevforholdResource elevforhold, Date currentTime) {
        String resoursID = elevforhold.getSystemId().getIdentifikatorverdi();
        Periode gyldighetsperiode = elevforhold.getGyldighetsperiode();
        String status = gyldighetsperiodeService.isValid(gyldighetsperiode,currentTime)
                ?"ACTIV"
                :"DISABLED";

        log.info("Systemid: " + resoursID + " stop: " + elevforhold.getGyldighetsperiode().getSlutt() + "Status: " + status);

        return status;
    }


    public enum UserType {
        EMPLOYEESTAFF,
        EMPLOYEEFACULTY,
        STUDENT,
        AFFILIATE
    }

}

