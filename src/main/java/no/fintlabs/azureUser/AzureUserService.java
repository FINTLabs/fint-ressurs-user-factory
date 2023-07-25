package no.fintlabs.azureUser;

import lombok.extern.slf4j.Slf4j;
import no.fintlabs.resourceServices.PersonService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AzureUserService {
    private final PersonService personService;

    public AzureUserService(PersonService personService) {
        this.personService = personService;
    }

    public void updateUserEntity(AzureUser azureUser) {
      log.info("Updating user : " + azureUser.getUserPrincipalName());

    }
}
