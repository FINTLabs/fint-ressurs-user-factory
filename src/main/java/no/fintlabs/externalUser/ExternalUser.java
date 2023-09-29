package no.fintlabs.externalUser;

import lombok.*;
import no.fintlabs.user.UserUtils;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class ExternalUser {
    private Long id;
    private String firstName;
    private String lastName;
    @Builder.Default
    private String userType = String.valueOf(UserUtils.UserType.EXTERNAL);
    private String userName;
    private UUID identityProviderUserObjectId;
    private String mainOrganisationUnitName;
    private String mainOrganisationUnitId;
    private String mobilePhone;
    private String email;

    public boolean isValid(){
        return true;
    }
}
