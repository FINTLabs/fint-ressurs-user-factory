package no.fintlabs.resourceServices;

import no.fint.model.resource.felles.PersonResource;
import no.fint.model.resource.utdanning.elev.ElevResource;
import no.fintlabs.cache.FintCache;
import no.fintlabs.links.ResourceLinkUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PersonUtdanningService {
    private final FintCache<String,PersonResource> personUtdanningResourceCache;

    public PersonUtdanningService(@Qualifier("personResourceUtdanningCache")
                                  FintCache<String, PersonResource> personUtdanningResourceCache, FintCache<String, PersonResource> personUtdanningResourceCache1) {
        this.personUtdanningResourceCache = personUtdanningResourceCache1;
    }

    public Optional<PersonResource> getPersonUtdanning(ElevResource elevResource){
        return personUtdanningResourceCache.getOptional(
                ResourceLinkUtil.getFirstLink(
                        elevResource::getPerson,
                        elevResource,
                        "Elev"
                )
        );

    }
}
