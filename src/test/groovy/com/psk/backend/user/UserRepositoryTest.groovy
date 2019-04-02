package com.psk.backend.user

import com.psk.backend.PageableBuilder
import com.psk.backend.user.value.NewUserForm
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

import javax.annotation.Resource

@SpringBootTest
@ActiveProfiles("test")
class UserRepositoryTest extends Specification {

    @Resource
    MongoOperations operations

    @Resource
    UserRepository userRepository

    def cleanup() {
        operations.remove(new Query(), User)
    }

    def "should create new user"() {
        setup:
        def form = new NewUserForm(
                name: "name",
                surname: "surname",
                role: "ROLE_USER",
                email: "email"
        )

        when:
        def result = userRepository.insert(form)

        then:
        result.isSuccess()
        def loaded = operations.findById(result.getOrElse().id, User)

        loaded.name == "name"
        loaded.surname == "surname"
        loaded.email == "email"
        loaded.role == UserRole.ROLE_USER
    }

    def "should load user list view"() {
        setup:
        def user = UserBuilder.user()
        def page = PageableBuilder.pageable()
        operations.insert(user)

        when:
        def result = userRepository.list(page)
        def loaded = result.getContent().get(0)

        then:
        result.totalElements == 1
        loaded.role == user.role.toString()
        loaded.name == user.name
        loaded.surname == user.surname
        loaded.email == user.email
        loaded.role == user.role.toString()
    }
}
