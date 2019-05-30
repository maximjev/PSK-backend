package com.psk.backend.auth.value;


import com.psk.backend.domain.user.UserRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CurrentUserView {
    private String id;
    private String email;
    private String name;
    private String surname;
    private UserRole role;
}
