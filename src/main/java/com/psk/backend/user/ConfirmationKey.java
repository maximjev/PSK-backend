package com.psk.backend.user;



import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.UUID;

@Document
@EqualsAndHashCode
@ToString
@Getter
@Setter
public class ConfirmationKey {
    @Id
    private String id;
    private String userId;
    private boolean valid;
    private LocalDateTime validTill;

    public ConfirmationKey(String userId){
        this.userId = userId;
        valid = true;
        validTill = LocalDateTime.now().plusDays(1);
    }
    public boolean isValid(){
        if (validTill.isBefore(LocalDateTime.now()) || !valid) {
            return false;
        }
        return true;
    }
}
