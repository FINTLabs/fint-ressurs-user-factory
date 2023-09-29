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

    public boolean isValid(){
        if (this.getEmployeeId() == null && this.getStudentId() == null){
            return false;
        }
        return true;
    }


}
