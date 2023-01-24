package no.fintlabs.resourceServices;

import lombok.extern.slf4j.Slf4j;
import no.fint.model.resource.utdanning.elev.ElevResource;
import no.fintlabs.cache.FintCache;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ElevService {
    private final FintCache<String, ElevResource> elevResourceCache;
    private final GyldighetsperiodeService gyldighetsperiodeService;


    public ElevService(
            FintCache<String, ElevResource> elevResourceCache,
            GyldighetsperiodeService gyldighetsperiodeService
    ) {
        this.elevResourceCache = elevResourceCache;
        this.gyldighetsperiodeService = gyldighetsperiodeService;
    }

    public List<ElevResource> getAllValidElever(Date currentTime){
        List<ElevResource> elevResources = elevResourceCache
                .getAllDistinct()
                .stream()
                .filter(elevResource -> !elevResource.getElevforhold().isEmpty())
                .toList();
        return elevResources;
    }

}
