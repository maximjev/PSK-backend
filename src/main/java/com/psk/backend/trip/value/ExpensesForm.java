package com.psk.backend.trip.value;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

@Getter
@Setter
public class ExpensesForm {
    @PositiveOrZero
    private Long count;

    @PositiveOrZero
    private BigDecimal price;

    @NotNull
    private boolean isOrdered;
}
