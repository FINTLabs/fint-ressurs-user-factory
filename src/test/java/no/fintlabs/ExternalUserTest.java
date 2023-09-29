package no.fintlabs;

import no.fintlabs.externalUser.ExternalUser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExternalUserTest {


    @Test
    public void shouldReturnExternalUserWithUserType(){
        ExternalUser externalUser = ExternalUser.builder()
                .build();

        String userType = "EXTERNAL";

        assertEquals(userType,externalUser.getUserType());
    }

}