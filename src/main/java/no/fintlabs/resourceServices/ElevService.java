package no.fintlabs.resourceServices;

import no.fint.model.resource.utdanning.elev.ElevResource;
import no.fintlabs.cache.FintCache;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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

    }
}
