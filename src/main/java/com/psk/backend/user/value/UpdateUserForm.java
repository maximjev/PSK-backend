package com.psk.backend.user.value;

import com.psk.backend.user.validation.ValidUserRole;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class UpdateUserForm {

    @NotEmpty
    private String id;

    @NotEmpty
    private String name;

    @NotEmpty
    private String surname;

    @NotEmpty
    private String email;

    @NotEmpty
    @ValidUserRole
    private String role;
}
