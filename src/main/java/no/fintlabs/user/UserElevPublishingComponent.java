package no.fintlabs.user;

import lombok.extern.slf4j.Slf4j;
import no.fintlabs.resourceServices.ElevService;
import no.fintlabs.resourceServices.PersonUtdanningService;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class UserElevPublishingComponent {
    private final ElevService elevService;
    private final PersonUtdanningService personUtdanningService;
    private final UserEntityProducerService userEntityProducerService;

    public UserElevPublishingComponent(
            ElevService elevService,
            PersonUtdanningService personUtdanningService,
            UserEntityProducerService userEntityProducerService
    ){
        this.elevService = elevService;
        this.personUtdanningService = personUtdanningService;
        this.userEntityProducerService = userEntityProducerService;
    }

    public void publishElevUsers(){
        Date currentTime = Date.from(Instant.now());

        List
    }
}
