package com.psk.backend

import groovy.transform.builder.Builder
import groovy.transform.builder.ExternalStrategy
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

@Builder(builderStrategy = ExternalStrategy, forClass = PageRequest)
class PageableBuilder {
    static Pageable pageable() {
        return new PageRequest(0, 5, new Sort())
    }

}
