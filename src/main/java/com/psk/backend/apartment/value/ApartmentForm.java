package com.psk.backend.apartment.value;


import com.psk.backend.common.address.AddressForm;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ApartmentForm {

    private AddressForm address;

    private String name;

    @NotNull
    @Positive
    private Long size;
}
