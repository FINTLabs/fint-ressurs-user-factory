package no.fintlabs.arbeidforhold;

import lombok.extern.slf4j.Slf4j;
import no.fint.model.resource.administrasjon.personal.ArbeidsforholdResource;
import no.fintlabs.cache.FintCache;
import org.springframework.stereotype.Service;

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

    public Optional<ArbeidsforholdResource> getNewestArbeidsforhold(List<String> arbeidsforholdListe){
        List<ArbeidsforholdResource> afl = null;
        for (String af:arbeidsforholdListe) {
            afl.add(arbeidsforholdResourceCache.getOptional(af));
        }

        return afl.stream().max(Comparator.comparing(p -> p.getGyldighetsperiode().getStart()));
    }
}

