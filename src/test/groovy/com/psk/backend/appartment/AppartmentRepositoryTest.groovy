package com.psk.backend.appartment

import com.psk.backend.appartment.value.AppartmentForm
import com.psk.backend.common.address.AddressForm
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

import javax.annotation.Resource

import static com.psk.backend.PageableBuilder.pageable
import static com.psk.backend.appartment.AppartmentBuilder.appartment

@SpringBootTest
@ActiveProfiles("test")
//@Ignore
class AppartmentRepositoryTest extends Specification {

    @Resource
    MongoOperations operations

    @Resource
    AppartmentRepository repository

    def cleanup() {
        operations.remove(new Query(), Appartment)
    }

    def "should create new appartment"() {
        setup:
        def form = new AppartmentForm(
                address: new AddressForm(
                        city: 'Vilnius',
                        street: 'Gedimino',
                        appartmentNumber: '5'
                ),
                size: 6
        )
        def appartment = appartment()

        when:
        def result = repository.insert(form)

        then:
        result.isSuccess()
        def loaded = operations.findById(result.getOrElse().id, Appartment)

        loaded.address.street == appartment.address.street
        loaded.address.city == appartment.address.city
        loaded.address.appartmentNumber == appartment.address.appartmentNumber
        loaded.size == appartment.size
    }

    def "should load appartment list view"() {
        setup:
        def appartment = appartment()
        def page = pageable()
        operations.insert(appartment)

        when:
        def result = repository.list(page)
        def loaded = result.getContent().get(0)

        then:
        result.totalElements == 1
        loaded.address.street == appartment.address.street
        loaded.address.city == appartment.address.city
        loaded.address.appartmentNumber == appartment.address.appartmentNumber
        loaded.size == appartment.size
    }

    def "should update appartment"() {
        setup:
        def appartment = appartment()
        def id = '123'
        appartment.id = id
        operations.insert(appartment)
        def form = new AppartmentForm(
                address: new AddressForm(
                        city: 'Vilnius',
                        street: 'Gedimino',
                        appartmentNumber: '5'
                ),
                size: 5
        )

        when:
        repository.update(id, form)
        def result = repository.findById(id)

        then:
        result.isSuccess()
        def loaded = operations.findById(result.getOrElse().id, Appartment)
        loaded.size == 5
        loaded.address.street == appartment.address.street
        loaded.address.city == appartment.address.city
        loaded.address.appartmentNumber == appartment.address.appartmentNumber
    }
}
