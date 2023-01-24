package no.fintlabs.resourceServices;

import no.fint.model.resource.Link;
import no.fint.model.resource.administrasjon.organisasjon.OrganisasjonselementResource;
import no.fint.model.resource.administrasjon.personal.ArbeidsforholdResource;
import no.fintlabs.cache.FintCache;
import no.fintlabs.links.ResourceLinkUtil;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ArbeidsforholdService {

    private final GyldighetsperiodeService gyldighetsperiodeService;
    private final FintCache<String, ArbeidsforholdResource> arbeidsforholdResourceCache;
    private final FintCache<String, OrganisasjonselementResource> organisasjonselementResourceCache;

    public ArbeidsforholdService(
            GyldighetsperiodeService gyldighetsperiodeService,
            FintCache<String, ArbeidsforholdResource> arbeidsforholdResourceCache,
            FintCache<String, OrganisasjonselementResource> organisasjonselementResourceCache
    ) {
        this.gyldighetsperiodeService = gyldighetsperiodeService;
        this.arbeidsforholdResourceCache = arbeidsforholdResourceCache;
        this.organisasjonselementResourceCache = organisasjonselementResourceCache;
    }

    public Optional<ArbeidsforholdResource> getArbeidsforhold(
            Collection<Link> arbeidsforholdLinks,
            Date currentTime
    ) {
        List<ArbeidsforholdResource> arbeidsforholdResources = arbeidsforholdLinks
                .stream()
                .map(Link::getHref)
                .map(ResourceLinkUtil::systemIdToLowerCase)
                .map(arbeidsforholdResourceCache::getOptional)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
        return getValidMainArbeidsforhold(arbeidsforholdResources, currentTime)
                .or(() -> getValidNonMainArbeidsforhold(arbeidsforholdResources, currentTime));
    }

    private Optional<ArbeidsforholdResource> getValidMainArbeidsforhold(
            List<ArbeidsforholdResource> arbeidsforholdResources, Date currentTime) {
        return arbeidsforholdResources
                .stream()
                .filter(ArbeidsforholdResource::getHovedstilling)
                .filter(arbeidsforholdResource -> isValid(arbeidsforholdResource, currentTime))
                .findFirst();
    }

    private Optional<ArbeidsforholdResource> getValidNonMainArbeidsforhold(
            List<ArbeidsforholdResource> arbeidsforholdResources, Date currentTime) {
        return arbeidsforholdResources
                .stream()
                .filter(arbeidsforholdResource -> !arbeidsforholdResource.getHovedstilling())
                .filter(arbeidsforholdResource -> isValid(arbeidsforholdResource, currentTime))
                .max(Comparator.comparingLong(ArbeidsforholdResource::getAnsettelsesprosent));
    }

    private boolean isValid(ArbeidsforholdResource arbeidsforholdResource, Date currentTime) {
        return gyldighetsperiodeService.isValid(
                arbeidsforholdResource.getArbeidsforholdsperiode() != null
                        ? arbeidsforholdResource.getArbeidsforholdsperiode()
                        : arbeidsforholdResource.getGyldighetsperiode(),
                currentTime
        );
    }

    public Optional<String> getLederHref(OrganisasjonselementResource arbeidssted) {
        return ResourceLinkUtil.getOptionalFirstLink(arbeidssted::getLeder);
    }

    public Optional<String> getOrganisasjonsnavn(OrganisasjonselementResource arbeidssted) {
        return Optional.ofNullable(arbeidssted.getOrganisasjonsnavn());
    }

    public Optional<OrganisasjonselementResource> getArbeidssted(ArbeidsforholdResource arbeidsforholdResource, Date currentTime) {
        return ResourceLinkUtil.getOptionalFirstLink(arbeidsforholdResource::getArbeidssted)
                .flatMap(organisasjonselementResourceCache::getOptional)
                .filter(organisasjonselementResource ->
                        gyldighetsperiodeService.isValid(organisasjonselementResource.getGyldighetsperiode(), currentTime));
    }

}
