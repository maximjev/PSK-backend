package com.psk.backend.trip


import com.psk.backend.appartment.reservation.Reservation
import com.psk.backend.appartment.reservation.ReservationRepository
import com.psk.backend.trip.value.TripForm
import com.psk.backend.trip.value.TripUserForm
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.context.ActiveProfiles
import spock.lang.Ignore
import spock.lang.Specification

import javax.annotation.Resource

import static com.psk.backend.appartment.AppartmentBuilder.appartment
import static com.psk.backend.user.UserBuilder.user
import static java.time.LocalDateTime.of

@SpringBootTest
@ActiveProfiles("test")
@Ignore
class TripManagementServiceTest extends Specification {

    @Resource
    MongoOperations operations

    @Resource
    TripRepository tripRepository

    @Resource
    ReservationRepository reservationRepository

    @Resource
    TripManagementService service

    def cleanup() {
        operations.remove(new Query(), Trip)
        operations.remove(new Query(), Reservation)
    }

    def "Should create new trip and reservation"() {

        setup:
        def user1 = user('1')
        def user2 = user('2')
        operations.insertAll([user1, user2])

        def source = appartment('ap-1')
        def destination = appartment('ap-2')
        operations.insertAll([source, destination])


        def form = new TripForm(
                name: "name",
                source: 'ap-1',
                destination: 'ap-2',
                description: 'description',
                departion: of(2019, 7, 1, 12, 0),
                reservationBegin: of(2019, 7, 3, 12, 0),
                reservationEnd: of(2019, 7, 8, 12, 0),
                users: [new TripUserForm(userId: '1',inAppartment: true), new TripUserForm(userId: '2', inAppartment: true)]
        )


        when:
        def result = service.create(form)

        then:
        result.isSuccess()
        def loadedTrip = operations.findById(result.getOrElse().id, Trip)
        def loadedReservation = reservationRepository.findByTripId(result.getOrElse().id)

        loadedTrip.name == form.name
        loadedTrip.departion == form.departion
        loadedTrip.reservationBegin == form.reservationBegin
        loadedTrip.reservationEnd == form.reservationEnd
        loadedTrip.users.size() == 2
        loadedTrip.users[0].id == user1.id
        loadedTrip.users[0].name == user1.name
        loadedTrip.users[0].surname == user1.surname
        loadedTrip.users[0].email == user1.email

        loadedReservation.isSuccess()
        loadedReservation.getOrElse().appartmentId == 'ap-2'
        loadedReservation.getOrElse().places == 2
        loadedReservation.getOrElse().from == form.reservationBegin
        loadedReservation.getOrElse().till == form.reservationEnd
    }
}
