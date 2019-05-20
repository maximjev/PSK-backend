package com.psk.backend.appartment.reservation

import com.psk.backend.appartment.Appartment
import com.psk.backend.appartment.reservation.value.PlacementFilter
import com.psk.backend.appartment.reservation.value.PlacementResult
import com.psk.backend.user.User
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.context.ActiveProfiles
import spock.lang.Ignore
import spock.lang.Specification

import javax.annotation.Resource
import java.time.LocalDateTime

import static com.psk.backend.appartment.AppartmentBuilder.appartment
import static com.psk.backend.appartment.reservation.ReservationBuilder.reservation

@SpringBootTest
@ActiveProfiles("test")
@Ignore
class ReservationRepositoryTest extends Specification {

    @Resource
    MongoOperations operations

    @Resource
    ReservationRepository repository

    User creator

    def cleanup() {
        operations.remove(new Query(), Reservation)
        operations.remove(new Query(), Appartment)
    }

    def "should calculate available reservations when filtering between"() {
        setup:
        def appartment = appartment()
        def id = '123'
        appartment.id = id
        operations.insert(appartment, "appartment")


        def reservations = [
                reservation('1', '2019-01-01 12:00', '2019-01-09 12:00'),
                reservation('2', '2019-01-02 12:00', '2019-01-10 12:00')
        ]
        operations.insertAll(reservations)

        def filter = new PlacementFilter(
                from: LocalDateTime.of(2019, 1, 3, 12, 0),
                till: LocalDateTime.of(2019, 1, 6, 12, 0)
        )
        def expected = new PlacementResult(4, 2)
        expected.calculateAvailablePlaces(6)

        when:
        def result = repository.availablePlaces(id, filter)

        then:
        result.isSuccess()
        def placementResult = result.getOrElse(null)
        placementResult.availablePlaces == expected.availablePlaces
        placementResult.reservations == expected.reservations
        placementResult.takenPlaces == expected.takenPlaces
    }

    def "should calculate available reservations on left intersection"() {
        setup:
        def appartment = appartment()
        def id = '123'
        appartment.id = id
        operations.insert(appartment, "appartment")


        def reservations = [
                reservation('1', '2019-01-01 12:00', '2019-01-04 12:00'),
                reservation('2', '2019-01-02 12:00', '2019-01-05 12:00')
        ]
        operations.insertAll(reservations)

        def filter = new PlacementFilter(
                from: LocalDateTime.of(2019, 1, 3, 12, 0),
                till: LocalDateTime.of(2019, 1, 6, 12, 0)
        )
        def expected = new PlacementResult(4, 2)
        expected.calculateAvailablePlaces(6)

        when:
        def result = repository.availablePlaces(id, filter)

        then:
        result.isSuccess()
        def placementResult = result.getOrElse(null)
        placementResult.availablePlaces == expected.availablePlaces
        placementResult.reservations == expected.reservations
        placementResult.takenPlaces == expected.takenPlaces
    }

    def "should calculate available reservations on right intersection"() {
        setup:
        def appartment = appartment()
        def id = '123'
        appartment.id = id
        operations.insert(appartment, "appartment")


        def reservations = [
                reservation('1', '2019-01-05 12:00', '2019-01-15 12:00'),
                reservation('2', '2019-01-06 12:00', '2019-01-16 12:00')
        ]
        operations.insertAll(reservations)

        def filter = new PlacementFilter(
                from: LocalDateTime.of(2019, 1, 3, 12, 0),
                till: LocalDateTime.of(2019, 1, 7, 12, 0)
        )
        def expected = new PlacementResult(4, 2)
        expected.calculateAvailablePlaces(6)

        when:
        def result = repository.availablePlaces(id, filter)

        then:
        result.isSuccess()
        def placementResult = result.getOrElse(null)
        placementResult.availablePlaces == expected.availablePlaces
        placementResult.reservations == expected.reservations
        placementResult.takenPlaces == expected.takenPlaces
    }

    def "should calculate available reservations on full intersection"() {
        setup:
        def appartment = appartment()
        def id = '123'
        appartment.id = id
        operations.insert(appartment, "appartment")


        def reservations = [
                reservation('1', '2019-01-05 12:00', '2019-01-15 12:00'),
                reservation('2', '2019-01-06 12:00', '2019-01-16 12:00')
        ]
        operations.insertAll(reservations)

        def filter = new PlacementFilter(
                from: LocalDateTime.of(2019, 1, 3, 12, 0),
                till: LocalDateTime.of(2019, 1, 22, 12, 0)
        )
        def expected = new PlacementResult(4, 2)
        expected.calculateAvailablePlaces(6)

        when:
        def result = repository.availablePlaces(id, filter)

        then:
        result.isSuccess()
        def placementResult = result.getOrElse(null)
        placementResult.availablePlaces == expected.availablePlaces
        placementResult.reservations == expected.reservations
        placementResult.takenPlaces == expected.takenPlaces
    }
}
