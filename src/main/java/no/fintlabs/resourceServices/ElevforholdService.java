package no.fintlabs.resourceServices;

import lombok.extern.slf4j.Slf4j;
import no.fint.model.resource.Link;
import no.fint.model.resource.utdanning.elev.ElevforholdResource;
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

    public ElevforholdService(
            GyldighetsperiodeService gyldighetsperiodeService,

            FintCache<String, ElevforholdResource> elevforholdResourceCache) {
        this.gyldighetsperiodeService = gyldighetsperiodeService;

        this.elevforholdResourceCache = elevforholdResourceCache;
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
                .filter(elevforholdResource -> isValid(elevforholdResource,currentTime))
                .findFirst();
    }



    private boolean isValid(ElevforholdResource elevforholdResource, Date currentTime){

        if (elevforholdResource.getGyldighetsperiode() != null) {
            return gyldighetsperiodeService.isValid(
                    elevforholdResource.getGyldighetsperiode(),
                    currentTime);
        } else  return false;
    }


}
