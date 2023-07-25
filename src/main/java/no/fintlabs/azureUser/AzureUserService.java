package no.fintlabs.azureUser;

import lombok.extern.slf4j.Slf4j;
import no.fintlabs.cache.FintCache;
import no.fintlabs.resourceServices.PersonService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class AzureUserService {
    private final PersonService personService;
    private final FintCache<String,AzureUser> azureUserCache;

    public AzureUserService(PersonService personService, FintCache<String, AzureUser> azureUserCache) {
        this.personService = personService;
        this.azureUserCache = azureUserCache;
    }

    public void updateUserEntity(AzureUser azureUser) {
      log.info("Updating user : " + azureUser.getUserPrincipalName());

    }

    public Map<String,String> getAzureUserAttributes(String employeeIdORStudentId){
        Map<String,String> azureUserAttributes = new HashMap<>();

        AzureUser azureUser = azureUserCache.getOptional(employeeIdORStudentId).orElse(null);
        if (azureUser != null){
            azureUserAttributes.put("email", azureUser.getMail());
            azureUserAttributes.put("userName", azureUser.getUserPrincipalName());
            azureUserAttributes.put("identityProviderUserObjectId", azureUser.getId());
        }
        else {
            log.info("No match for employeeId or studentId {} in azureusercache", employeeIdORStudentId);
        }
        return azureUserAttributes;
    }
}
