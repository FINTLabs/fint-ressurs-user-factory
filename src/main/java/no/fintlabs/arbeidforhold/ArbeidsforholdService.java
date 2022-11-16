package no.fintlabs.arbeidforhold;

import lombok.extern.slf4j.Slf4j;
import no.fint.model.resource.administrasjon.personal.ArbeidsforholdResource;
import no.fintlabs.cache.FintCache;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ArbeidsforholdService {
    private final FintCache<String, ArbeidsforholdResource> arbeidsforholdResourceCache;

    public ArbeidsforholdService(FintCache<String, ArbeidsforholdResource> arbeidsforholdResourceCache) {
        this.arbeidsforholdResourceCache = arbeidsforholdResourceCache;
    }
    public Optional<ArbeidsforholdResource> getNewestArbeidsforhold(List<String> arbeidsforholdHrefs){
        List<ArbeidsforholdResource> arbeidsforholdRessurs = new ArrayList<>();

        for (String arbeidsforholdHref : arbeidsforholdHrefs) {
            try {
                ArbeidsforholdResource arbeidsforholdResourceFromCache = arbeidsforholdResourceCache.get(arbeidsforholdHref);
                arbeidsforholdRessurs.add(arbeidsforholdResourceFromCache);
            } catch (Exception e) {
                log.info("Fant ikke arbeidsforhold i cache med nøkkel :" + arbeidsforholdHref);
            }
        }
        return arbeidsforholdRessurs.stream().max(Comparator.comparing(p -> p.getGyldighetsperiode().getStart()));
    }
}

