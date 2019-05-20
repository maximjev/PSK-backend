package com.psk.backend.appartment.reservation;

import com.psk.backend.appartment.reservation.value.ReservationListView;
import com.psk.backend.config.BaseMapperConfig;
import com.psk.backend.user.AuditUser;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = BaseMapperConfig.class)
public abstract class ReservationMapper {

    abstract ReservationListView listView(Reservation reservation);

    public String map(AuditUser user) {
        return user.toString();
    }

    public abstract Reservation update(Reservation newReservation, @MappingTarget Reservation reservation);
}
