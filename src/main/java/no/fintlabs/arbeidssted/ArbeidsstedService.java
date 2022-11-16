package no.fintlabs.arbeidssted;


import lombok.extern.slf4j.Slf4j;
import no.fint.model.resource.administrasjon.organisasjon.OrganisasjonselementResource;
import no.fint.model.resource.administrasjon.personal.ArbeidsforholdResource;
import no.fintlabs.cache.FintCache;
import no.fintlabs.links.ResourceLinkUtil;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class ArbeidsstedService {
    private final FintCache<String, ArbeidsforholdResource> arbeidsforholdResourceCache;
    private final FintCache<String, OrganisasjonselementResource> organisasjonselementResourceCache;


    public ArbeidsstedService(FintCache<String, ArbeidsforholdResource> arbeidsforholdResourceCache,
                              FintCache<String, OrganisasjonselementResource> organisasjonselementResourceCache) {
        this.arbeidsforholdResourceCache = arbeidsforholdResourceCache;
        this.organisasjonselementResourceCache = organisasjonselementResourceCache;
    }


    public String getLeder(Optional<ArbeidsforholdResource> currentArbeidsforhold) {

        String arbeidsstedHref = currentArbeidsforhold.get().getArbeidssted().get(0).toString();
        OrganisasjonselementResource organisasjonselementResource = organisasjonselementResourceCache.get(arbeidsstedHref);

        return organisasjonselementResource.getLeder().get(0).toString();
    }
}
