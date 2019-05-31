package com.psk.backend.domain.user;



import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document
@EqualsAndHashCode
@ToString
@Getter
@Setter
public class ConfirmationKey {
    @Id
    private String id;
    private String userId;
    private LocalDateTime validTill;

    public ConfirmationKey(String userId){
        this.userId = userId;
        validTill = LocalDateTime.now().plusDays(1);
    }
    public boolean isValid(){
        return validTill.isAfter(LocalDateTime.now());
    }
}
