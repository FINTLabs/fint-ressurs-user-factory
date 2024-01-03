package no.fintlabs.user;

import no.fint.model.felles.kompleksedatatyper.Periode;
import no.fint.model.resource.administrasjon.personal.PersonalressursResource;
import no.fintlabs.resourceServices.GyldighetsperiodeService;

import java.util.Date;

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

    public enum UserType {
        EMPLOYEE,
        STUDENT,
        EXTERNAL
    }

}
