package com.psk.backend.trip.value;

import com.psk.backend.common.validation.ValidAppartment;
import com.psk.backend.trip.validation.ValidTripDates;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ValidTripDates
public class TripForm {

    @NotEmpty
    private String name;

    @ValidAppartment
    private String source;

    @ValidAppartment
    private String destination;

    @NotNull
    private List<TripUserForm> users;

    private String description;

    private ExpensesForm flight;

    private ExpensesForm hotel;

    @NotNull
    private LocalDateTime departion;

    @NotNull
    private LocalDateTime reservationBegin;

    @NotNull
    private LocalDateTime reservationEnd;

    @PositiveOrZero
    private BigDecimal otherExpenses;

    public Long getUserInAppartmentCount() {
        return getUsers()
                .stream()
                .filter(TripUserForm::isInAppartment)
                .count();
    }
}
