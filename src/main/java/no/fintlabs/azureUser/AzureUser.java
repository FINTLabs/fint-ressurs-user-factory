package no.fintlabs.azureUser;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class AzureUser {
    private String id;
    private String mail;
    private String userPrincipalName;
    private String employeeId;
    private String studentId;
    private boolean accountEnabled;

    public boolean isValid(){
        return this.getEmployeeId() != null || this.getStudentId() != null;
    }


}
