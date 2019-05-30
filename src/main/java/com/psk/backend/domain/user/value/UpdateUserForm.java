package com.psk.backend.domain.user.value;

import com.psk.backend.domain.validation.ValidUserRole;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class UpdateUserForm {
    @NotEmpty
    private String name;
    @NotEmpty
    private String surname;

    @NotEmpty
    @ValidUserRole
    private String role;

    @NotNull
    private LocalDateTime updatedAt;
}
