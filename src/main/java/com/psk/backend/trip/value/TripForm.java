package com.psk.backend.trip.value;

import com.psk.backend.trip.validation.ValidTripDates;
import com.psk.backend.trip.validation.ValidTripUsers;
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
@ValidTripUsers
public class TripForm {

    @NotEmpty
    private String name;

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
    private boolean reservation;

    @PositiveOrZero
    private BigDecimal otherExpenses;

    @NotNull
    private LocalDateTime updatedAt;
}
