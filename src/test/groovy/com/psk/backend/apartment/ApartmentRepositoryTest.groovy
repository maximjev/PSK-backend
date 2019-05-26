package com.psk.backend.apartment

import com.psk.backend.apartment.value.ApartmentForm
import com.psk.backend.common.address.AddressForm
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

import javax.annotation.Resource

import static com.psk.backend.PageableBuilder.pageable
import static com.psk.backend.apartment.ApartmentBuilder.apartment

@SpringBootTest
@ActiveProfiles("test")
class ApartmentRepositoryTest extends Specification {

    @Resource
    MongoOperations operations

    @Resource
    ApartmentRepository repository

    def cleanup() {
        operations.remove(new Query(), Apartment)
    }

    def "should create new apartment"() {
        setup:
        def form = new ApartmentForm(
                address: new AddressForm(
                        city: 'Vilnius',
                        street: 'Naugarduko',
                        apartmentNumber: '24'
                ),
                size: 6
        )
        def apartment = apartment()

        when:
        def result = repository.insert(form)

        then:
        result.isSuccess()
        def loaded = operations.findById(result.getOrElse().id, Apartment)

        loaded.address.street == apartment.address.street
        loaded.address.city == apartment.address.city
        loaded.address.apartmentNumber == apartment.address.apartmentNumber
        loaded.size == apartment.size
    }

    def "should load apartment list view"() {
        setup:
        def apartment = apartment()
        def page = pageable()
        operations.insert(apartment)

        when:
        def result = repository.list(page)
        def loaded = result.getContent().get(0)

        then:
        result.totalElements == 1
        loaded.address.street == apartment.address.street
        loaded.address.city == apartment.address.city
        loaded.address.apartmentNumber == apartment.address.apartmentNumber
        loaded.size == apartment.size
    }

    def "should update apartment"() {
        setup:
        def apartment = apartment()
        def id = '123'
        apartment.id = id
        operations.insert(apartment)
        def form = new ApartmentForm(
                address: new AddressForm(
                        city: 'Vilnius',
                        street: 'Naugarduko',
                        apartmentNumber: '24'
                ),
                size: 5
        )

        when:
        repository.update(id, form)
        def result = repository.findById(id)

        then:
        result.isSuccess()
        def loaded = operations.findById(result.getOrElse().id, Apartment)
        loaded.size == 5
        loaded.address.street == apartment.address.street
        loaded.address.city == apartment.address.city
        loaded.address.apartmentNumber == apartment.address.apartmentNumber
    }
}
