package no.fintlabs.externalUser;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class AzureExternalUser {
    String firstName;
    String lastName;
    String mobilePhone;
    String email;
    String mainOrganisationUnitName;
    String mainOrganisationUnitId;
    String userName;
    String idpUserObjectId;
    String userPrincipalName;

    public boolean isValid() { return true;
    }
}
