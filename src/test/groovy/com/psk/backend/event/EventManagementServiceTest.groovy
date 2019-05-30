package com.psk.backend.event

import com.psk.backend.domain.calendar.value.EventForm
import com.psk.backend.repository.TripRepository
import com.psk.backend.domain.trip.value.TripCreateForm
import com.psk.backend.domain.trip.value.TripUserForm
import com.psk.backend.domain.user.User
import com.psk.backend.service.EventManagementService
import com.psk.backend.service.TripConfirmationService
import com.psk.backend.service.TripManagementService
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Query
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

import javax.annotation.Resource


import static java.time.LocalDateTime.of

@SpringBootTest
@ActiveProfiles("test")
class EventManagementServiceTest extends Specification {

    @Resource
    MongoOperations operations

    @Resource
    TripRepository tripRepository

    @Resource
    TripManagementService tripService

    @Resource
    EventManagementService eventService

    @Resource
    TripConfirmationService confirmationService

    User owner

    Authentication auth

    def setup() {
        owner = user('1')
        operations.insert(owner)

        auth = new TestingAuthenticationToken(owner.getEmail(), owner.getPassword())
        auth.authenticated = true
        SecurityContextHolder.getContext().authentication = auth
    }
    def cleanup() {
        SecurityContextHolder.getContext().authentication = null
        operations.remove(new Query(), Trip)
        operations.remove(new Query(), Event)
        operations.remove(new Query(), Apartment)
        operations.remove(new Query(), User)
    }

    def "should create event for user"() {
        setup:
        def form = new EventForm(
                description: 'description',
                start: of(2019, 7, 3, 12, 0),
                end: of(2019, 7, 8, 12, 0)
        )

        when:
        def result = eventService.create(form, auth)
        def event = operations.findById(result.getOrElse().id, Event.class)

        then:
        result.isSuccess()
        event.users[0].id == owner.id
        event.users[0].email == owner.email
        event.users[0].name == owner.name
        event.users[0].surname == owner.surname
        event.users[0].status == EventUserStatus.CONFIRMED
        event.createdBy == AuditUser.of(owner)
        event.description == form.description
        event.start == form.start
        event.end == form.end
    }

    def "should return created event list for user"() {
        setup:
        def form = new EventForm(
                description: 'description',
                start: of(2019, 7, 3, 12, 0),
                end: of(2019, 7, 8, 12, 0)
        )

        when:
        def result = eventService.create(form, auth)
        def list = eventService.list(auth)

        then:
        result.isSuccess()
        list[0].id == result.getOrElse().id
        list[0].start == form.start
        list[0].end == form.end
        list[0].name == form.name
        list[0].owner
        !list[0].trip
    }

    def "should return trip event list for user in apartment"() {
        setup:

        def source = apartment('ap-1')
        def destination = apartment('ap-2')
        operations.insertAll([source, destination])
        operations.insert(user('2', 'newEmail'))

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
        def result = tripService.create(form)
        def list = eventService.list(auth)

        then:
        result.isSuccess()
        list[0].id == result.getOrElse().id
        list[0].start == form.departure
        list[0].end == form.reservationBegin
        list[0].name == form.name
        !list[0].owner
        list[0].trip
    }

    def "should return trip event list for user in hotel"() {
        setup:

        def source = apartment('ap-1')
        def destination = apartment('ap-2')
        operations.insertAll([source, destination])
        operations.insert(user('2', 'newEmail'))

        def form = new TripCreateForm(
                name: "name",
                source: 'ap-1',
                destination: 'ap-2',
                description: 'description',
                reservation: false,
                departure: of(2019, 7, 1, 12, 0),
                arrival: of(2019, 7, 3, 12, 0),
                users: [new TripUserForm(userId: '1', inApartment: false, carRent: 'car rent', residenceAddress: 'Naugarduko 24'),
                        new TripUserForm(userId: '2', inApartment: false, flightTicket: 'flight ticket', residenceAddress: 'Naugarduko 24')]
        )

        when:
        def result = tripService.create(form)
        def list = eventService.list(auth)

        then:
        result.isSuccess()
        list[0].id == result.getOrElse().id
        list[0].start == form.departure
        list[0].end == form.arrival
        list[0].name == form.name
        !list[0].owner
        list[0].trip
    }

    def "should return both user and trip events list"() {
        setup:

        def source = apartment('ap-1')
        def destination = apartment('ap-2')
        operations.insertAll([source, destination])
        operations.insert(user('2', 'newEmail'))

        def tripForm = new TripCreateForm(
                name: "name",
                source: 'ap-1',
                destination: 'ap-2',
                description: 'description',
                reservation: false,
                departure: of(2019, 7, 1, 12, 0),
                arrival: of(2019, 7, 3, 12, 0),
                users: [new TripUserForm(userId: '1', inApartment: false, carRent: 'car rent', residenceAddress: 'Naugarduko 24'),
                        new TripUserForm(userId: '2', inApartment: false, flightTicket: 'flight ticket', residenceAddress: 'Naugarduko 24')]
        )

        def form = new EventForm(
                description: 'description',
                start: of(2019, 7, 3, 12, 0),
                end: of(2019, 7, 8, 12, 0)
        )

        when:
        def trip = tripService.create(tripForm)
        def event = eventService.create(form, auth)
        def list = eventService.list(auth)

        then:
        event.isSuccess()
        trip.isSuccess()

        list[0].id == event.getOrElse().id
        list[0].start == form.start
        list[0].end == form.end
        list[0].name == form.name
        list[0].owner
        !list[0].trip

        list[1].id == trip.getOrElse().id
        list[1].start == tripForm.departure
        list[1].end == tripForm.arrival
        list[1].name == tripForm.name
        !list[1].owner
        list[1].trip
    }

    def "should not return declined trip event"() {
        setup:

        def source = apartment('ap-1')
        def destination = apartment('ap-2')
        operations.insertAll([source, destination])
        operations.insert(user('2', 'newEmail'))

        def tripForm = new TripCreateForm(
                name: "name",
                source: 'ap-1',
                destination: 'ap-2',
                description: 'description',
                reservation: false,
                departure: of(2019, 7, 1, 12, 0),
                arrival: of(2019, 7, 3, 12, 0),
                users: [new TripUserForm(userId: '1', inApartment: false, carRent: 'car rent', residenceAddress: 'Naugarduko 24'),
                        new TripUserForm(userId: '2', inApartment: false, flightTicket: 'flight ticket', residenceAddress: 'Naugarduko 24')]
        )

        when:
        def trip = tripService.create(tripForm)
        def declined = confirmationService.decline(trip.getOrElse().id, auth)
        def list = eventService.list(auth)

        then:
        trip.isSuccess()
        declined.isSuccess()

        list.size() == 0
    }

    def "should delete event for user"() {
        setup:
        def form = new EventForm(
                description: 'description',
                start: of(2019, 7, 3, 12, 0),
                end: of(2019, 7, 8, 12, 0)
        )

        when:
        def result = eventService.create(form, auth)
        def deleted = eventService.delete(result.getOrElse().id, auth)

        then:
        result.isSuccess()
        deleted.isSuccess()
    }
}
