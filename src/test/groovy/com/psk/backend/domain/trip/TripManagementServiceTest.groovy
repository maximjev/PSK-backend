package com.psk.backend.domain.trip

import com.psk.backend.domain.apartment.Apartment
import com.psk.backend.domain.reservation.Reservation
import com.psk.backend.domain.reservation.ReservationRepository
import com.psk.backend.domain.common.address.AddressFormatter
import com.psk.backend.service.TripManagementService
import com.psk.backend.domain.trip.value.TripCreateForm
import com.psk.backend.domain.trip.value.TripForm
import com.psk.backend.domain.trip.value.TripUserForm
import com.psk.backend.domain.user.User
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

import javax.annotation.Resource

import static com.psk.backend.domain.apartment.ApartmentBuilder.apartment
import static com.psk.backend.domain.reservation.ReservationBuilder.reservation
import static com.psk.backend.domain.user.UserBuilder.user
import static java.time.LocalDateTime.of

@SpringBootTest
@ActiveProfiles("test")
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
        operations.remove(new Query(), Apartment)
        operations.remove(new Query(), User)
    }

    def "Should create new trip and reservation"() {

        setup:
        def user1 = user('1')
        def user2 = user('2')
        operations.insertAll([user1, user2])

        def source = apartment('ap-1')
        def destination = apartment('ap-2')
        operations.insertAll([source, destination])


        def form = new TripCreateForm(
                name: "name",
                source: 'ap-1',
                destination: 'ap-2',
                description: 'description',
                reservation: true,
                departure: of(2019, 7, 1, 12, 0),
                reservationBegin: of(2019, 7, 3, 12, 0),
                reservationEnd: of(2019, 7, 8, 12, 0),
                users: [new TripUserForm(userId: '1', inApartment: true, carRent: 'car rent'),
                        new TripUserForm(userId: '2', inApartment: true, flightTicket: 'flight ticket')]
        )


        when:
        def result = service.create(form)

        then:
        result.isSuccess()
        def loadedTrip = operations.findById(result.getOrElse().id, Trip)
        def loadedReservation = reservationRepository.findByTripId(result.getOrElse().id)

        loadedTrip.name == form.name
        loadedTrip.departure == form.departure
        loadedTrip.destination.id == form.destination
        loadedTrip.source.id == form.source
        loadedTrip.reservationBegin == form.reservationBegin
        loadedTrip.reservationEnd == form.reservationEnd
        loadedTrip.users.size() == 2
        loadedTrip.users[0].id == user1.id
        loadedTrip.users[0].name == user1.name
        loadedTrip.users[0].surname == user1.surname
        loadedTrip.users[0].email == user1.email
        loadedTrip.users[0].carRent == 'car rent'
        loadedTrip.users[1].flightTicket == 'flight ticket'

        loadedReservation.isSuccess()
        loadedReservation.getOrElse().apartmentId == 'ap-2'
        loadedReservation.getOrElse().places == 2
        loadedReservation.getOrElse().from == form.reservationBegin
        loadedReservation.getOrElse().till == form.reservationEnd
    }

    def "Should update trip and reservation"() {

        setup:
        def user1 = user('1')
        def user2 = user('2')
        operations.insertAll([user1, user2])

        def source = apartment('ap-1')
        def destination = apartment('ap-2')
        def app = apartment('ap-3')
        operations.insertAll([source, destination, app])


        def createForm = new TripCreateForm(
                name: "name",
                source: 'ap-1',
                destination: 'ap-2',
                description: 'description',
                reservation: true,
                departure: of(2019, 7, 1, 12, 0),
                reservationBegin: of(2019, 7, 3, 12, 0),
                reservationEnd: of(2019, 7, 8, 12, 0),
                users: [new TripUserForm(userId: '1', inApartment: true), new TripUserForm(userId: '2', inApartment: true)]
        )

        def id = service.create(createForm).getOrElse().id
        def trip = operations.findById(id, Trip.class)
        when:
        def updateForm = new TripForm(
                name: "name-2",
                description: 'description-2',
                reservation: true,
                departure: of(2019, 8, 1, 12, 0),
                reservationBegin: of(2019, 8, 5, 12, 0),
                reservationEnd: of(2019, 8, 9, 12, 0),
                users: [new TripUserForm(userId: '2', inApartment: true)],
                updatedAt: trip.updatedAt
        )
        def result = service.update(id, updateForm)

        then:
        result.isSuccess()
        def loadedTrip = operations.findById(result.getOrElse().id, Trip)
        def loadedReservation = reservationRepository.findByTripId(result.getOrElse().id)

        loadedTrip.name == updateForm.name
        loadedTrip.departure == updateForm.departure
        loadedTrip.reservationBegin == updateForm.reservationBegin
        loadedTrip.reservationEnd == updateForm.reservationEnd
        loadedTrip.users.size() == 1
        loadedTrip.users[0].id == user2.id
        loadedTrip.users[0].name == user2.name
        loadedTrip.users[0].surname == user2.surname
        loadedTrip.users[0].email == user2.email

        loadedReservation.isSuccess()
        loadedReservation.getOrElse().apartmentId == 'ap-2'
        loadedReservation.getOrElse().places == 1
        loadedReservation.getOrElse().from == updateForm.reservationBegin
        loadedReservation.getOrElse().till == updateForm.reservationEnd
    }

    def "Should create new trip without reservation"() {

        setup:
        def user1 = user('1')
        def user2 = user('2')
        operations.insertAll([user1, user2])

        def source = apartment('ap-1')
        def destination = apartment('ap-2')
        operations.insertAll([source, destination])


        def form = new TripCreateForm(
                name: "name",
                source: 'ap-1',
                destination: 'ap-2',
                description: 'description',
                departure: of(2019, 7, 1, 12, 0),
                arrival: of(2019, 7, 2, 12, 0),
                reservation: false,
                users: [new TripUserForm(userId: '1', inApartment: true), new TripUserForm(userId: '2', inApartment: true)]
        )

        when:
        def result = service.create(form)

        then:
        result.isSuccess()
        def loadedTrip = operations.findById(result.getOrElse().id, Trip)
        def loadedReservation = reservationRepository.findByTripId(result.getOrElse().id)

        loadedTrip.name == form.name
        loadedTrip.departure == form.departure
        loadedTrip.destination.id == form.destination
        loadedTrip.source.id == form.source
        loadedTrip.reservationBegin == form.reservationBegin
        loadedTrip.reservationEnd == form.reservationEnd
        loadedTrip.users.size() == 2
        loadedTrip.users[0].id == user1.id
        loadedTrip.users[0].name == user1.name
        loadedTrip.users[0].surname == user1.surname
        loadedTrip.users[0].email == user1.email
        loadedTrip.users[0].inApartment

        loadedReservation.isFailure()
    }

    def "Should not create new trip when no places available"() {

        setup:
        operations.insertAll([user('1'), user('2')])
        operations.insertAll([apartment('ap-1'), apartment('ap-2')])

        def reservations = [
                reservation('1', '2019-07-01 12:00', '2019-07-09 12:00', 'ap-2'),
                reservation('2', '2019-07-06 12:00', '2019-07-10 12:00', 'ap-2'),
                reservation('3', '2019-07-03 12:00', '2019-07-07 12:00', 'ap-2'),
        ]
        operations.insertAll(reservations)

        def form = new TripCreateForm(
                name: "name",
                source: 'ap-1',
                destination: 'ap-2',
                description: 'description',
                departure: of(2019, 7, 1, 12, 0),
                reservationBegin: of(2019, 7, 3, 12, 0),
                reservationEnd: of(2019, 7, 8, 12, 0),
                reservation: true,
                users: [new TripUserForm(userId: '1', inApartment: true), new TripUserForm(userId: '2', inApartment: true)]
        )

        when:
        def result = service.create(form)

        then:
        result.isFailure()
    }

    def "Should create new trip with reservation when users are mixed"() {

        setup:
        operations.insertAll([user('1'), user('2'), user('3')])

        operations.insertAll([apartment('ap-1'), apartment('ap-2')])

        def reservations = [
                reservation('1', '2019-07-01 12:00', '2019-07-09 12:00', 'ap-2'),
                reservation('2', '2019-07-06 12:00', '2019-07-10 12:00', 'ap-2'),
        ]
        operations.insertAll(reservations)

        def form = new TripCreateForm(
                name: "name",
                source: 'ap-1',
                destination: 'ap-2',
                description: 'description',
                departure: of(2019, 7, 1, 12, 0),
                reservationBegin: of(2019, 7, 3, 12, 0),
                reservationEnd: of(2019, 7, 8, 12, 0),
                reservation: true,
                users: [new TripUserForm(userId: '1', inApartment: false), new TripUserForm(userId: '2', inApartment: true)]
        )

        when:
        def result = service.create(form)

        then:
        result.isSuccess()
        def loadedTrip = operations.findById(result.getOrElse().id, Trip)
        def loadedReservation = reservationRepository.findByTripId(result.getOrElse().id)

        loadedTrip.users.size() == 2
        loadedTrip.users[0].id == '1'
        !loadedTrip.users[0].inApartment
        loadedTrip.users[1].id == '2'
        loadedTrip.users[1].inApartment
        loadedReservation.getOrElse().places == 1
    }

    def "Should delete both trip and reservation"() {

        setup:
        operations.insertAll([user('1'), user('2'), user('3')])

        operations.insertAll([apartment('ap-1'), apartment('ap-2')])

        def reservations = [
                reservation('1', '2019-07-01 12:00', '2019-07-09 12:00', 'ap-2'),
                reservation('2', '2019-07-06 12:00', '2019-07-10 12:00', 'ap-2'),
        ]
        operations.insertAll(reservations)

        def form = new TripCreateForm(
                name: "name",
                source: 'ap-1',
                destination: 'ap-2',
                description: 'description',
                departure: of(2019, 7, 1, 12, 0),
                reservationBegin: of(2019, 7, 3, 12, 0),
                reservationEnd: of(2019, 7, 8, 12, 0),
                reservation: true,
                users: [new TripUserForm(userId: '1', inApartment: false), new TripUserForm(userId: '2', inApartment: true)]
        )

        when:
        def tripId = service.create(form)
        def result = service.delete(tripId.getOrElse().id)
        then:
        result.isSuccess()
        def loadedTrip = tripRepository.findById(result.getOrElse().id)
        def loadedReservation = reservationRepository.findByTripId(result.getOrElse().id)

        loadedTrip.isFailure()
        loadedReservation.isFailure()
    }


    def "Should assign apartment address to user that is in apartment"() {

        setup:
        def user1 = user('1')
        def user2 = user('2')
        operations.insertAll([user1, user2])

        def source = apartment('ap-1')
        def destination = apartment('ap-2')
        operations.insertAll([source, destination])


        def form = new TripCreateForm(
                name: "name",
                source: 'ap-1',
                destination: 'ap-2',
                description: 'description',
                reservation: true,
                departure: of(2019, 7, 1, 12, 0),
                reservationBegin: of(2019, 7, 3, 12, 0),
                reservationEnd: of(2019, 7, 8, 12, 0),
                users: [new TripUserForm(userId: '1', inApartment: true, carRent: 'car rent'),
                        new TripUserForm(userId: '2', inApartment: true, flightTicket: 'flight ticket')]
        )


        when:
        def result = service.create(form)

        then:
        result.isSuccess()
        def loadedTrip = operations.findById(result.getOrElse().id, Trip)
        def loadedReservation = reservationRepository.findByTripId(result.getOrElse().id)

        loadedTrip.name == form.name
        loadedTrip.departure == form.departure
        loadedTrip.destination.id == form.destination
        loadedTrip.source.id == form.source
        loadedTrip.reservationBegin == form.reservationBegin
        loadedTrip.reservationEnd == form.reservationEnd
        loadedTrip.users.size() == 2
        loadedTrip.users[0].id == user1.id
        loadedTrip.users[0].name == user1.name
        loadedTrip.users[0].surname == user1.surname
        loadedTrip.users[0].email == user1.email
        loadedTrip.users[0].carRent == 'car rent'
        loadedTrip.users[1].flightTicket == 'flight ticket'
        loadedTrip.users[0].residenceAddress == AddressFormatter.formatAddress(loadedTrip.destination.address)
        loadedTrip.users[1].residenceAddress == AddressFormatter.formatAddress(loadedTrip.destination.address)

        loadedReservation.isSuccess()
        loadedReservation.getOrElse().apartmentId == 'ap-2'
        loadedReservation.getOrElse().places == 2
        loadedReservation.getOrElse().from == form.reservationBegin
        loadedReservation.getOrElse().till == form.reservationEnd
    }

    def "Should map residence address for users"() {

        setup:
        def user1 = user('1')
        def user2 = user('2')
        operations.insertAll([user1, user2])

        def source = apartment('ap-1')
        def destination = apartment('ap-2')
        operations.insertAll([source, destination])


        def form = new TripCreateForm(
                name: "name",
                source: 'ap-1',
                destination: 'ap-2',
                description: 'description',
                reservation: true,
                departure: of(2019, 7, 1, 12, 0),
                reservationBegin: of(2019, 7, 3, 12, 0),
                reservationEnd: of(2019, 7, 8, 12, 0),
                users: [new TripUserForm(userId: '1', inApartment: false, carRent: 'car rent', residenceAddress: 'Naugarduko 24, Vilnius'),
                        new TripUserForm(userId: '2', inApartment: false, flightTicket: 'flight ticket', residenceAddress: 'Naugarduko 25, Vilnius')]
        )


        when:
        def result = service.create(form)

        then:
        result.isSuccess()
        def loadedTrip = operations.findById(result.getOrElse().id, Trip)
        def loadedReservation = reservationRepository.findByTripId(result.getOrElse().id)

        loadedTrip.name == form.name
        loadedTrip.departure == form.departure
        loadedTrip.destination.id == form.destination
        loadedTrip.source.id == form.source
        loadedTrip.reservationBegin == form.reservationBegin
        loadedTrip.reservationEnd == form.reservationEnd
        loadedTrip.users.size() == 2
        loadedTrip.users[0].id == user1.id
        loadedTrip.users[0].name == user1.name
        loadedTrip.users[0].surname == user1.surname
        loadedTrip.users[0].email == user1.email
        loadedTrip.users[0].carRent == 'car rent'
        loadedTrip.users[1].flightTicket == 'flight ticket'
        loadedTrip.users[0].residenceAddress == 'Naugarduko 24, Vilnius'
        loadedTrip.users[1].residenceAddress == 'Naugarduko 25, Vilnius'

        loadedReservation.isSuccess()
        loadedReservation.getOrElse().apartmentId == 'ap-2'
        loadedReservation.getOrElse().places == 0
        loadedReservation.getOrElse().from == form.reservationBegin
        loadedReservation.getOrElse().till == form.reservationEnd
    }

    def "Should map trip information to view"() {

        setup:
        def user1 = user('1')
        def user2 = user('2')
        operations.insertAll([user1, user2])

        def source = apartment('ap-1')
        def destination = apartment('ap-2')
        operations.insertAll([source, destination])


        def form = new TripCreateForm(
                name: "name",
                source: 'ap-1',
                destination: 'ap-2',
                description: 'description',
                reservation: true,
                departure: of(2019, 7, 1, 12, 0),
                reservationBegin: of(2019, 7, 3, 12, 0),
                reservationEnd: of(2019, 7, 8, 12, 0),
                users: [new TripUserForm(userId: '1', inApartment: true, carRent: 'car rent'),
                        new TripUserForm(userId: '2', inApartment: false, flightTicket: 'flight ticket', residenceAddress: 'Naugarduko 25, Vilnius')]
        )


        when:
        def result = service.create(form)

        then:
        result.isSuccess()
        def trip = operations.findById(result.getOrElse().id, Trip)
        def loadedTrip = tripRepository.get(result.getOrElse().id).getOrElse()

        loadedTrip.name == form.name
        loadedTrip.departure == form.departure
        loadedTrip.destination.city == trip.destination.address.city
        loadedTrip.destination.street == trip.destination.address.street
        loadedTrip.destination.apartmentNumber == trip.destination.address.apartmentNumber
        loadedTrip.source.city == trip.source.address.city
        loadedTrip.source.street== trip.source.address.street
        loadedTrip.source.apartmentNumber== trip.source.address.apartmentNumber
        loadedTrip.reservationBegin == form.reservationBegin
        loadedTrip.reservationEnd == form.reservationEnd
        loadedTrip.users.size() == 2
        loadedTrip.users[0].userId == user1.id
        loadedTrip.users[0].inApartment
        loadedTrip.users[0].residenceAddress == AddressFormatter.formatAddress(trip.destination.address)
        loadedTrip.users[0].carRent == 'car rent'
        !loadedTrip.users[1].inApartment
        loadedTrip.users[1].userId == user2.id
        loadedTrip.users[1].flightTicket == 'flight ticket'
        loadedTrip.users[1].residenceAddress == 'Naugarduko 25, Vilnius'
    }
}
