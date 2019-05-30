package com.psk.backend.domain.apartment.value;

import com.psk.backend.domain.common.address.AddressView;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@EqualsAndHashCode
@Getter
@Setter
@ToString
public class ApartmentListView {
    private String id;
    private String name;
    private AddressView address;
    private Long size;
}
