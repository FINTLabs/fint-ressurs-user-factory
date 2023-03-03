package no.fintlabs.resourceServices

import no.fint.model.felles.kompleksedatatyper.Periode
import spock.lang.Specification

import java.time.LocalDate
import java.time.ZoneId

import static java.util.Date.from

class GyldighetsperiodeServiceTest extends Specification {


    def "isValid should return true if gyldighetsperiode is not null and current time is within the valid period"() {
        given:
        def gyldighetsperiodeService = new GyldighetsperiodeService()
        def currentTime = from(LocalDate.of(2020, 10, 31)
                .atStartOfDay(ZoneId.of('Z')).toInstant())
        def startTime = from(LocalDate.of(2020, 8, 1)
                .atStartOfDay(ZoneId.of('Z')).toInstant())
        def sluttTime = from(LocalDate.of(2020, 12, 31)
                .atStartOfDay(ZoneId.of('Z')).toInstant())

        def gyldighetsperiode = new Periode(
                start: startTime,
                slutt: sluttTime
        )

        when:
        def result = gyldighetsperiodeService.isValid(gyldighetsperiode, currentTime)


        then:
        result == true
    }

    def "isValid should throw NullPeriodeException if gyldighetsperiode is null"() {
        given:
        def gyldighetsperiodeService = new GyldighetsperiodeService()
        def currentTime = from(LocalDate.of(2020, 10, 31)
                .atStartOfDay(ZoneId.of('Z')).toInstant())


        def gyldighetsperiode = new Periode()


        when:
        gyldighetsperiodeService.isValid(null, currentTime)

        then:
        thrown(GyldighetsperiodeService.NullPeriodeException)
    }

    def "isValid should return false if gyldighetsperiode is not null but current time is after the end date"() {
        given:
        def gyldighetsperiodeService = new GyldighetsperiodeService()
        def currentTime = from(LocalDate.of(2020, 10, 31)
                .atStartOfDay(ZoneId.of('Z')).toInstant())
        def startTime = from(LocalDate.of(2022, 8, 1)
                .atStartOfDay(ZoneId.of('Z')).toInstant())
        def sluttTime = from(LocalDate.of(2020, 12, 31)
                .atStartOfDay(ZoneId.of('Z')).toInstant())

        def gyldighetsperiode = new Periode(
                start: startTime,
                slutt: sluttTime
        )


        when:
        def result = gyldighetsperiodeService.isValid(gyldighetsperiode, currentTime)

        then:
        result == false
    }

    def "isValid should return true if gyldighetsperiode is not null and end date is null"() {
        given:
        def gyldighetsperiodeService = new GyldighetsperiodeService()
        def currentTime = from(LocalDate.of(2020, 10, 31)
                .atStartOfDay(ZoneId.of('Z')).toInstant())
        def startTime = from(LocalDate.of(2020, 8, 1)
                .atStartOfDay(ZoneId.of('Z')).toInstant())
        def sluttTime = null

        def gyldighetsperiode = new Periode(
                start: startTime,
                slutt: sluttTime
        )

        when:
        def result = gyldighetsperiodeService.isValid(gyldighetsperiode, currentTime)

        then:
        result == true
    }
}
