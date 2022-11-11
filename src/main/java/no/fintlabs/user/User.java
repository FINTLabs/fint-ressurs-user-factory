package no.fintlabs.user;

import lombok.Builder;
import lombok.Data;
import no.fint.model.resource.administrasjon.personal.PersonalressursResource;
import no.fint.model.resource.felles.PersonResource;
import no.fintlabs.cache.FintCache;
import no.fintlabs.cache.FintCacheEvent;
import no.fintlabs.links.ResourceLinkUtil;
import org.springframework.stereotype.Service;

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

