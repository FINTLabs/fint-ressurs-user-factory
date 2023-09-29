package no.fintlabs.externalUser;

import lombok.*;

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
    private String userType;
    private String userName;
    private UUID identityProviderUserObjectId;
    private String mainOrganisationUnitName;
    private String mainOrganisationUnitId;
    private String mobilePhone;
    private String email;
}
