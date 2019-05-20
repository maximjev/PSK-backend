package com.psk.backend.appartment.value;

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
    private AddressForm address;
    private Long size;
}
