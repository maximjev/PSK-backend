package com.psk.backend.appartment.value;

import com.psk.backend.appartment.Address;
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
    private Address address;
    private Integer size;
}
