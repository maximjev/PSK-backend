package com.psk.backend.trip


import com.psk.backend.appartment.reservation.Reservation
import com.psk.backend.trip.value.TripForm
import com.psk.backend.trip.value.TripUserForm
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

import javax.annotation.Resource

import static java.time.LocalDateTime.of

@SpringBootTest
@ActiveProfiles("test")
//@Ignore
class TripRepositoryTest extends Specification {

    @Resource
    MongoOperations operations

    @Resource
    TripRepository tripRepository

    def cleanup() {
        operations.remove(new Query(), Trip)
        operations.remove(new Query(), Reservation)
    }

    def "Should create new trip and reservation"() {
        setup:
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

    }
}
