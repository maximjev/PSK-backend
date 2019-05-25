package com.psk.backend.appartment.reservation;

import com.psk.backend.appartment.reservation.value.ReservationListView;
import com.psk.backend.config.BaseMapperConfig;
import com.psk.backend.trip.value.TripForm;
import com.psk.backend.user.AuditUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = BaseMapperConfig.class)
public abstract class ReservationMapper {

    abstract ReservationListView listView(Reservation reservation);

    public String map(AuditUser user) {
        return user.toString();
    }

    public abstract Reservation update(Reservation newReservation, @MappingTarget Reservation reservation);

    @Mapping(source = "reservationBegin", target = "from")
    @Mapping(source = "reservationEnd", target = "till")
    @Mapping(source = "destination", target = "appartmentId")
    @Mapping(expression = "java((long)form.getUsers()" +
            ".stream()" +
            ".filter(f -> f.isInAppartment())" +
            ".count())", target = "places")
    public abstract Reservation fromTrip(TripForm form);
}
