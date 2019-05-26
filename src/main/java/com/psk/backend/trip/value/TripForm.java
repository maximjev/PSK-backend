package com.psk.backend.trip.value;

import com.psk.backend.common.validation.ValidApartment;
import com.psk.backend.trip.validation.ValidTripDates;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
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

    @NotEmpty
    @ValidApartment
    private String source;

    @NotEmpty
    @ValidApartment
    private String destination;

    @Valid
    private List<TripUserForm> users;

    private String description;

    private ExpensesForm flight;

    private ExpensesForm hotel;

    private ExpensesForm carRent;

    @NotNull
    private LocalDateTime departure;

    private LocalDateTime reservationBegin;

    private LocalDateTime reservationEnd;

    private LocalDateTime arrival;

    @NotNull
    private boolean noReservation;

    @PositiveOrZero
    private BigDecimal otherExpenses;
}
