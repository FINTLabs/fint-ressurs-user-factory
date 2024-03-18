package no.fintlabs.resourceServices;

import lombok.extern.slf4j.Slf4j;
import no.fintlabs.cache.FintCache;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SkoleressursService {
    private final FintCache<String,Long> employeeInSchoolCache;

    public SkoleressursService(FintCache<String, Long> employeeInSchoolCache) {
        this.employeeInSchoolCache = employeeInSchoolCache;
    }

    public boolean isEmployeeInSchool(String resourceId){
        return employeeInSchoolCache.containsKey(resourceId);
    }
}
