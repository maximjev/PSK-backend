package com.psk.backend.apartment;

import com.psk.backend.apartment.value.ApartmentForm;
import com.psk.backend.apartment.value.ApartmentListView;
import com.psk.backend.apartment.value.ApartmentSelectView;
import com.psk.backend.apartment.value.ApartmentView;
import com.psk.backend.config.BaseMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = BaseMapperConfig.class)
public interface ApartmentMapper {

    Apartment create(ApartmentForm form);

    ApartmentListView listView(Apartment apartment);

    ApartmentView view(Apartment apartment);

    ApartmentSelectView selectView(Apartment apartment);

    Apartment update(ApartmentForm form, @MappingTarget Apartment apartment);
}
