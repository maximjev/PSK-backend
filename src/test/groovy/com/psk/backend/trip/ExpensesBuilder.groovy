package com.psk.backend.trip

import groovy.transform.builder.Builder
import groovy.transform.builder.ExternalStrategy

@Builder(builderStrategy = ExternalStrategy, forClass = Expenses)
class ExpensesBuilder {
    ExpensesBuilder() {
        count(1)
        ordered(false)
        price(BigDecimal.valueOf(55.09))
    }

    static Expenses expenses() {
        new ExpensesBuilder().build()
    }
}
