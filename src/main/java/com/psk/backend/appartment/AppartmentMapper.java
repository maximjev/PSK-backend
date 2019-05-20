package com.psk.backend.appartment;

import com.psk.backend.appartment.value.AppartmentForm;
import com.psk.backend.appartment.value.AppartmentListView;
import com.psk.backend.appartment.value.AppartmentView;
import com.psk.backend.config.BaseMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = BaseMapperConfig.class)
public interface AppartmentMapper {

    Appartment create(AppartmentForm form);

    AppartmentListView listView(Appartment appartment);

    AppartmentView view(Appartment appartment);

    Appartment update(AppartmentForm form, @MappingTarget Appartment appartment);
}
