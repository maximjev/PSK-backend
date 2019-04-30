package com.psk.backend.user;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ConfirmationKeyService {

    private final ConfirmationKeyRepository repository;

    public ConfirmationKeyService(ConfirmationKeyRepository repository){
        this.repository = repository;
    }

    public ConfirmationKey generateConfirmationKey(String userId){
        ConfirmationKey key = new ConfirmationKey(userId);
        key.setValid(true);
        key.setValidTill(LocalDateTime.now().plusDays(1));
        repository.save(key);
        return key;
    }

    public Optional<ConfirmationKey> getConfirmationKeyByToken(String token){
        return repository.getByToken(token);
    }

    public void invalidate(String token){
        repository.invalidate(token);
    }

    public boolean validatePasswordResetToken(ConfirmationKey key) {
        if (key.getValidTill().isBefore(LocalDateTime.now()) || !key.isValid()) {
            return false;
        }
        return true;
    }

}
