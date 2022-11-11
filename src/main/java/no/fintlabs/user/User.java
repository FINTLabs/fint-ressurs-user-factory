package no.fintlabs.user;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class User {

    private String userId;
    private String fintSelfLink;
    private String firstName;
    private String lastName;
    private String userType;
    private String userName;
    private List<RoleRefs> roleRefs;
    private String mobilePhone;
    private String email;
    private  String managerRef;
}

