package com.psk.backend.appartment.value;

import com.psk.backend.common.address.AddressView;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@EqualsAndHashCode
@Getter
@Setter
@ToString
public class AppartmentListView {
    private String id;
    private String name;
    private AddressView address;
    private Long size;
}
