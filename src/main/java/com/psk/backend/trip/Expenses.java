package com.psk.backend.trip;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class Expenses {
    private Long count;
    private BigDecimal price;
}
