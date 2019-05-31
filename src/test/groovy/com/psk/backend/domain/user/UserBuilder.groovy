package com.psk.backend.domain.user

import groovy.transform.builder.Builder
import groovy.transform.builder.ExternalStrategy

@Builder(builderStrategy = ExternalStrategy, forClass = User)
class UserBuilder {
    UserBuilder() {
        email('email@email.com')
        password('password')
        name('name')
        surname('surname')
        role(UserRole.ROLE_ADMIN)
    }

    static User user() {
        new UserBuilder().build()
    }

    static User user(String id) {
        new UserBuilder(
                id: id
        ).build()
    }

    static User user(String id, String email) {
        new UserBuilder(
                id: id,
                email: email
        ).build()
    }
}
