package no.fintlabs.azureUser;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AzureUser {
    private String id;
    private String mail;
    private String userPrincipalName;
    private String employeeId;
    private String studentId;
}
