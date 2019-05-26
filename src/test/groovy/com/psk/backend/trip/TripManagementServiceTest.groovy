package com.psk.backend.trip

import com.psk.backend.apartment.Apartment
import com.psk.backend.apartment.reservation.Reservation
import com.psk.backend.apartment.reservation.ReservationRepository
import com.psk.backend.trip.value.TripCreateForm
import com.psk.backend.trip.value.TripForm
import com.psk.backend.trip.value.TripMergeForm
import com.psk.backend.trip.value.TripUserForm
import com.psk.backend.user.User
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

import javax.annotation.Resource

import static com.psk.backend.apartment.ApartmentBuilder.apartment
import static com.psk.backend.apartment.reservation.ReservationBuilder.reservation
import static com.psk.backend.user.UserBuilder.user
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
        def updateForm = new TripForm(
                name: "name-2",
                description: 'description-2',
                reservation: true,
                departure: of(2019, 8, 1, 12, 0),
                reservationBegin: of(2019, 8, 5, 12, 0),
                reservationEnd: of(2019, 8, 9, 12, 0),
                users: [new TripUserForm(userId: '2', inApartment: true)]
        )

        when:
        def id = service.create(createForm).getOrElse().id
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
                reservation('3', '2019-07-03 12:00', '2019-07-05 12:00', 'ap-2'),
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

    def "Should not merge trips with too large date distance"() {

        setup:
        operations.insertAll([user('1'), user('2'), user('3'), user('4')])

        operations.insertAll([apartment('ap-1'), apartment('ap-2')])

        def reservations = [
                reservation('1', '2019-07-01 12:00', '2019-07-09 12:00', 'ap-2'),
                reservation('2', '2019-07-06 12:00', '2019-07-10 12:00', 'ap-2'),
        ]
        operations.insertAll(reservations)

        def form1 = new TripCreateForm(
                name: 'name 1',
                source: 'ap-1',
                destination: 'ap-2',
                description: 'description',
                departure: of(2019, 7, 1, 12, 0),
                reservationBegin: of(2019, 7, 3, 12, 0),
                reservationEnd: of(2019, 7, 8, 12, 0),
                reservation: false,
                users: [new TripUserForm(userId: '1', inApartment: false), new TripUserForm(userId: '2', inApartment: true)]
        )
        def form2 = new TripCreateForm(
                name: 'name 2',
                source: 'ap-1',
                destination: 'ap-2',
                description: 'description',
                departure: of(2019, 7, 3, 12, 0),
                reservationBegin: of(2019, 7, 3, 12, 0),
                reservationEnd: of(2019, 7, 8, 12, 0),
                reservation: false,
                users: [new TripUserForm(userId: '3', inApartment: false), new TripUserForm(userId: '4', inApartment: true)]
        )


        when:
        def firstTrip = service.create(form1)
        def secondTrip = service.create(form2)

        def mergeForm = new TripMergeForm(
                tripOne: firstTrip.getOrElse().id,
                tripTwo: secondTrip.getOrElse().id,
        )
        def result = service.merge(mergeForm)

        then:
        result.isFailure()
    }

    def "Should merge two trips with two reservations"() {

        setup:
        operations.insertAll([user('1'), user('2'), user('3'), user('4')])

        operations.insertAll([apartment('ap-1'), apartment('ap-2')])

        def reservations = [
                reservation('1', '2019-07-01 12:00', '2019-07-09 12:00', 'ap-2'),
                reservation('2', '2019-07-06 12:00', '2019-07-10 12:00', 'ap-2'),
        ]
        operations.insertAll(reservations)

        def form1 = new TripCreateForm(
                name: 'name 1',
                source: 'ap-1',
                destination: 'ap-2',
                description: 'description',
                departure: of(2019, 7, 1, 12, 0),
                reservationBegin: of(2019, 7, 3, 12, 0),
                reservationEnd: of(2019, 7, 8, 12, 0),
                reservation: true,
                users: [new TripUserForm(userId: '1', inApartment: false), new TripUserForm(userId: '2', inApartment: true)]
        )
        def form2 = new TripCreateForm(
                name: 'name 2',
                source: 'ap-1',
                destination: 'ap-2',
                description: 'description',
                departure: of(2019, 7, 2, 11, 0),
                reservationBegin: of(2019, 7, 3, 12, 0),
                reservationEnd: of(2019, 7, 8, 12, 0),
                reservation: true,
                users: [new TripUserForm(userId: '3', inApartment: false), new TripUserForm(userId: '4', inApartment: true)]
        )


        when:
        def firstTrip = service.create(form1)
        def secondTrip = service.create(form2)

        def mergeForm = new TripMergeForm(
                tripOne: firstTrip.getOrElse().id,
                tripTwo: secondTrip.getOrElse().id,
        )
        def result = service.merge(mergeForm)

        then:
        result.isSuccess()
        def mergedTrip = tripRepository.findById(result.getOrElse().id).getOrElse()
        def loadedReservation = reservationRepository.findByTripId(result.getOrElse().id).getOrElse()
        def deletedTrip = tripRepository.findById(secondTrip.getOrElse().id)
        def deletedReservation = reservationRepository.findByTripId(secondTrip.getOrElse().id)

        deletedTrip.isFailure()
        deletedReservation.isFailure()

        mergedTrip.source.id == form1.source
        mergedTrip.destination.id == form1.destination
        mergedTrip.reservationBegin == form1.reservationBegin
        mergedTrip.reservationEnd == form1.reservationEnd
        mergedTrip.departure == form1.departure
        mergedTrip.users.size() == 4

        loadedReservation.places == 2
        loadedReservation.apartmentId == form1.destination
        loadedReservation.from == form1.reservationBegin
        loadedReservation.till == form1.reservationEnd
    }

    def "Should merge two trips with only first trip reservation"() {

        setup:
        operations.insertAll([user('1'), user('2'), user('3'), user('4')])

        operations.insertAll([apartment('ap-1'), apartment('ap-2')])

        def reservations = [
                reservation('1', '2019-07-01 12:00', '2019-07-09 12:00', 'ap-2'),
                reservation('2', '2019-07-06 12:00', '2019-07-10 12:00', 'ap-2'),
        ]
        operations.insertAll(reservations)

        def form1 = new TripCreateForm(
                name: 'name 1',
                source: 'ap-1',
                destination: 'ap-2',
                description: 'description',
                departure: of(2019, 7, 1, 12, 0),
                reservationBegin: of(2019, 7, 3, 12, 0),
                reservationEnd: of(2019, 7, 8, 12, 0),
                reservation: true,
                users: [new TripUserForm(userId: '1', inApartment: false), new TripUserForm(userId: '2', inApartment: true)]
        )
        def form2 = new TripCreateForm(
                name: 'name 2',
                source: 'ap-1',
                destination: 'ap-2',
                description: 'description',
                departure: of(2019, 7, 2, 11, 0),
                arrival: of(2019, 7, 5, 11, 0),
                reservation: false,
                users: [new TripUserForm(userId: '3', inApartment: false), new TripUserForm(userId: '4', inApartment: false)]
        )


        when:
        def firstTrip = service.create(form1)
        def secondTrip = service.create(form2)

        def mergeForm = new TripMergeForm(
                tripOne: firstTrip.getOrElse().id,
                tripTwo: secondTrip.getOrElse().id,
        )
        def result = service.merge(mergeForm)

        then:
        result.isSuccess()
        def mergedTrip = tripRepository.findById(result.getOrElse().id).getOrElse()
        def loadedReservation = reservationRepository.findByTripId(result.getOrElse().id).getOrElse()
        def deletedTrip = tripRepository.findById(secondTrip.getOrElse().id)
        def deletedReservation = reservationRepository.findByTripId(secondTrip.getOrElse().id)

        deletedTrip.isFailure()
        deletedReservation.isFailure()

        mergedTrip.source.id == form1.source
        mergedTrip.destination.id == form1.destination
        mergedTrip.reservationBegin == form1.reservationBegin
        mergedTrip.reservationEnd == form1.reservationEnd
        mergedTrip.departure == form1.departure
        mergedTrip.users.size() == 4

        loadedReservation.places == 1
        loadedReservation.apartmentId == form1.destination
        loadedReservation.from == form1.reservationBegin
        loadedReservation.till == form1.reservationEnd
    }

    def "Should merge two trips with only second trip reservation"() {

        setup:
        operations.insertAll([user('1'), user('2'), user('3'), user('4')])

        operations.insertAll([apartment('ap-1'), apartment('ap-2')])

        def reservations = [
                reservation('1', '2019-07-01 12:00', '2019-07-09 12:00', 'ap-2'),
                reservation('2', '2019-07-06 12:00', '2019-07-10 12:00', 'ap-2'),
        ]
        operations.insertAll(reservations)

        def form1 = new TripCreateForm(
                name: 'name 2',
                source: 'ap-1',
                destination: 'ap-2',
                description: 'description',
                departure: of(2019, 7, 2, 11, 0),
                arrival: of(2019, 7, 5, 11, 0),
                reservation: false,
                users: [new TripUserForm(userId: '3', inApartment: false), new TripUserForm(userId: '4', inApartment: false)]
        )
        def form2 = new TripCreateForm(
                name: 'name 1',
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
        def firstTrip = service.create(form1)
        def secondTrip = service.create(form2)

        def mergeForm = new TripMergeForm(
                tripOne: firstTrip.getOrElse().id,
                tripTwo: secondTrip.getOrElse().id,
        )
        def result = service.merge(mergeForm)

        then:
        result.isSuccess()
        def mergedTrip = tripRepository.findById(result.getOrElse().id).getOrElse()
        def loadedReservation = reservationRepository.findByTripId(result.getOrElse().id).getOrElse()
        def deletedTrip = tripRepository.findById(secondTrip.getOrElse().id)
        def deletedReservation = reservationRepository.findByTripId(secondTrip.getOrElse().id)

        deletedTrip.isFailure()
        deletedReservation.isFailure()

        mergedTrip.source.id == form1.source
        mergedTrip.destination.id == form1.destination
        mergedTrip.reservationBegin == form2.reservationBegin
        mergedTrip.reservationEnd == form2.reservationEnd
        mergedTrip.departure == form1.departure
        mergedTrip.users.size() == 4

        loadedReservation.places == 1
        loadedReservation.apartmentId == form1.destination
        loadedReservation.from == form2.reservationBegin
        loadedReservation.till == form2.reservationEnd
    }

    def "Should merge two trips without reservations"() {

        setup:
        operations.insertAll([user('1'), user('2'), user('3'), user('4')])

        operations.insertAll([apartment('ap-1'), apartment('ap-2')])

        def reservations = [
                reservation('1', '2019-07-01 12:00', '2019-07-09 12:00', 'ap-2'),
                reservation('2', '2019-07-06 12:00', '2019-07-10 12:00', 'ap-2'),
        ]
        operations.insertAll(reservations)

        def form1 = new TripCreateForm(
                name: 'name 2',
                source: 'ap-1',
                destination: 'ap-2',
                description: 'description',
                departure: of(2019, 7, 2, 11, 0),
                arrival: of(2019, 7, 5, 11, 0),
                reservation: false,
                users: [new TripUserForm(userId: '3', inApartment: false), new TripUserForm(userId: '4', inApartment: false)]
        )
        def form2 = new TripCreateForm(
                name: 'name 1',
                source: 'ap-1',
                destination: 'ap-2',
                description: 'description',
                departure: of(2019, 7, 1, 12, 0),
                arrival: of(2019, 7, 3, 12, 0),
                reservation: false,
                users: [new TripUserForm(userId: '1', inApartment: false), new TripUserForm(userId: '2', inApartment: false)]
        )

        when:
        def firstTrip = service.create(form1)
        def secondTrip = service.create(form2)

        def mergeForm = new TripMergeForm(
                tripOne: firstTrip.getOrElse().id,
                tripTwo: secondTrip.getOrElse().id,
        )
        def result = service.merge(mergeForm)

        then:
        result.isSuccess()
        def mergedTrip = tripRepository.findById(result.getOrElse().id).getOrElse()
        def deletedTrip = tripRepository.findById(secondTrip.getOrElse().id)

        deletedTrip.isFailure()

        mergedTrip.source.id == form1.source
        mergedTrip.destination.id == form1.destination
        mergedTrip.departure == form1.departure
        mergedTrip.arrival == form1.arrival
        mergedTrip.users.size() == 4
    }
}
