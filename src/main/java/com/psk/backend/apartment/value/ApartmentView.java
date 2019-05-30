package com.psk.backend.apartment.value;

import com.psk.backend.common.address.AddressForm;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@EqualsAndHashCode
@Getter
@Setter
@ToString
public class ApartmentView {
    private String id;
    private String name;
    private AddressForm address;
    private Long size;
    private LocalDateTime updatedAt;
}
