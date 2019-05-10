package com.psk.backend.appartment;

import com.psk.backend.appartment.value.AppartmentForm;
import com.psk.backend.appartment.value.AppartmentListView;
import com.psk.backend.config.BaseMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = BaseMapperConfig.class)
public interface AppartmentMapper {

    public Appartment create(AppartmentForm form);

    public AppartmentListView listView(Appartment appartment);

    public Appartment update(AppartmentForm form, @MappingTarget Appartment appartment);
}
