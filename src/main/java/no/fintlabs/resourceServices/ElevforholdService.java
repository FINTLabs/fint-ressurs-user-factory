package no.fintlabs.resourceServices;

import lombok.extern.slf4j.Slf4j;
import no.fint.model.resource.Link;
import no.fint.model.resource.administrasjon.organisasjon.OrganisasjonselementResource;
import no.fint.model.resource.utdanning.elev.ElevforholdResource;
import no.fint.model.resource.utdanning.utdanningsprogram.SkoleResource;
import no.fintlabs.cache.FintCache;
import no.fintlabs.links.ResourceLinkUtil;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ElevforholdService {
    private final GyldighetsperiodeService gyldighetsperiodeService;
    private final FintCache<String, ElevforholdResource> elevforholdResourceCache;
    private final FintCache<String, SkoleResource> skoleResourceCache;
    private final FintCache<String, OrganisasjonselementResource> organisasjonselementResourceCache;

    public ElevforholdService(
            GyldighetsperiodeService gyldighetsperiodeService,

            FintCache<String, ElevforholdResource> elevforholdResourceCache,
            FintCache<String, SkoleResource> skoleResourceCache,
            FintCache<String, OrganisasjonselementResource> organisasjonselementResourceCache) {
        this.gyldighetsperiodeService = gyldighetsperiodeService;
        this.elevforholdResourceCache = elevforholdResourceCache;
        this.skoleResourceCache = skoleResourceCache;
        this.organisasjonselementResourceCache = organisasjonselementResourceCache;
    }

    public Optional<ElevforholdResource> getElevforhold(
            Collection<Link> elevforholdLinks,
            Date currentTime
    ){
        List<ElevforholdResource> elevforholdResources = elevforholdLinks
                .stream()
                .map(Link::getHref)
                .map(ResourceLinkUtil::systemIdToLowerCase)
                .map(elevforholdResourceCache::getOptional)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
        return getValidElevforhold(elevforholdResources, currentTime);
    }


    private Optional<ElevforholdResource> getValidElevforhold(
            List<ElevforholdResource> elevforholdResources, Date currentTime
    ){
        return elevforholdResources
                .stream()
                //.filter(elevforholdResource -> isValid(elevforholdResource,currentTime))
                .filter(this::isHovedSkole)
                .findFirst();
    }


    private boolean isHovedSkole(ElevforholdResource elevforhold){
        if (elevforhold.getHovedskole()){
            return true;
        }else return false;
    }
    private boolean isValid(ElevforholdResource elevforholdResource, Date currentTime){

        if (elevforholdResource.getGyldighetsperiode() != null) {
            return gyldighetsperiodeService.isValid(
                    elevforholdResource.getGyldighetsperiode(),
                    currentTime);
        } else  return false;
    }


    public Optional<SkoleResource> getSkole(ElevforholdResource elevforhold, Date currentTime) {
        String skoleHref = elevforhold.getSkole().get(0).getHref().toLowerCase();
        Optional<SkoleResource> skoleResource = skoleResourceCache.getOptional(skoleHref);
        if (skoleResource.isEmpty()){log.info("Fant ikke skole for " + skoleHref);}
        return skoleResource;


    }

    public Optional<OrganisasjonselementResource> getSkoleOrgUnit(SkoleResource skoleResource, Date currentTime){
        // legg inn sjekk på null
        String skoleOrgUnitref = skoleResource.getOrganisasjon().get(0).getHref();
        //Legg på sjekk på null
        OrganisasjonselementResource organisasjonselementResource = organisasjonselementResourceCache
                .get(ResourceLinkUtil.organisasjonsKodeToLowerCase(skoleOrgUnitref));
        return Optional.ofNullable(organisasjonselementResource);
    }
}
