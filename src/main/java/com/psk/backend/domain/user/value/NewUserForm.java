package com.psk.backend.domain.user.value;

import com.psk.backend.domain.validation.ValidUserRole;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class NewUserForm {

    @NotEmpty
    private String name;

    @NotEmpty
    private String surname;

    @Email
    private String email;

    @NotEmpty
    @ValidUserRole
    private String role;
}
