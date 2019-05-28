package com.psk.backend.trip

import com.psk.backend.apartment.Apartment
import com.psk.backend.apartment.reservation.Reservation
import com.psk.backend.apartment.reservation.ReservationRepository
import com.psk.backend.trip.value.ExpensesForm
import com.psk.backend.trip.value.TripCreateForm
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
class TripMergingTest extends Specification {

    @Resource
    MongoOperations operations

    @Resource
    TripRepository tripRepository

    @Resource
    ReservationRepository reservationRepository

    @Resource
    TripManagementService service

    @Resource
    TripMergeService mergeService

    def cleanup() {
        operations.remove(new Query(), Trip)
        operations.remove(new Query(), Reservation)
        operations.remove(new Query(), Apartment)
        operations.remove(new Query(), User)
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
        def result = mergeService.merge(mergeForm)

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
        def result = mergeService.merge(mergeForm)

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
        def result = mergeService.merge(mergeForm)

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
        def result = mergeService.merge(mergeForm)

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
        def result = mergeService.merge(mergeForm)

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

    def "Should merge two trips with two reservations and flight expenses when only one is ordered"() {

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
                flight: new ExpensesForm(
                        count: 1,
                        price: 13,
                        isOrdered: true
                ),
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
                flight: new ExpensesForm(
                        count: 3,
                        price: 22.99,
                        isOrdered: false
                ),
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
        def result = mergeService.merge(mergeForm)

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

        mergedTrip.flight.count == 4
        mergedTrip.flight.price == 35.99
        !mergedTrip.flight.ordered

        loadedReservation.places == 2
        loadedReservation.apartmentId == form1.destination
        loadedReservation.from == form1.reservationBegin
        loadedReservation.till == form1.reservationEnd
    }

    def "Should merge two trips with two reservations, first has flight expenses and other has car sharing"() {

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
                flight: new ExpensesForm(
                        count: 1,
                        price: 13,
                        isOrdered: true
                ),
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
                carRent: new ExpensesForm(
                        count: 3,
                        price: 22.99,
                        isOrdered: false
                ),
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
        def result = mergeService.merge(mergeForm)

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

        mergedTrip.flight.count == 1
        mergedTrip.flight.price == 13.00
        mergedTrip.flight.ordered

        mergedTrip.carRent.count == 3
        mergedTrip.carRent.price == 22.99
        !mergedTrip.carRent.ordered


        loadedReservation.places == 2
        loadedReservation.apartmentId == form1.destination
        loadedReservation.from == form1.reservationBegin
        loadedReservation.till == form1.reservationEnd
    }

    def "Should merge two trips with two reservations and their description"() {

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
                description: 'description-1',
                flight: new ExpensesForm(
                        count: 1,
                        price: 13,
                        isOrdered: true
                ),
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
                description: 'description-2',
                carRent: new ExpensesForm(
                        count: 3,
                        price: 22.99,
                        isOrdered: false
                ),
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
        def result = mergeService.merge(mergeForm)

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

        mergedTrip.flight.count == 1
        mergedTrip.flight.price == 13.00
        mergedTrip.flight.ordered

        mergedTrip.carRent.count == 3
        mergedTrip.carRent.price == 22.99
        !mergedTrip.carRent.ordered

        mergedTrip.description == 'First trip description:\n' + form1.description + '\nSecond trip description:\n' + form2.description

        loadedReservation.places == 2
        loadedReservation.apartmentId == form1.destination
        loadedReservation.from == form1.reservationBegin
        loadedReservation.till == form1.reservationEnd
    }
}
