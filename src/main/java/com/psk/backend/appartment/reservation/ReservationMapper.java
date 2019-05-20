package com.psk.backend.appartment.reservation;

import com.psk.backend.appartment.reservation.value.ReservationListView;
import com.psk.backend.config.BaseMapperConfig;
import com.psk.backend.user.AuditUser;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapperConfig.class)
public abstract class ReservationMapper {

    abstract ReservationListView listView(Reservation reservation);

    public String map(AuditUser user) {
        return user.toString();
    }
}
