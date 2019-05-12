package com.psk.backend.appartment.value;


import com.psk.backend.appartment.Address;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class AppartmentForm {

    @NotEmpty
    private Address address;

    @NotEmpty
    @Positive
    private Integer size;
}
