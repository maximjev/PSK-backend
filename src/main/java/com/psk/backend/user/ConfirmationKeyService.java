package com.psk.backend.user;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ConfirmationKeyService {

    private final ConfirmationKeyRepository repository;

    public ConfirmationKeyService(ConfirmationKeyRepository repository){
        this.repository = repository;
    }

    public ConfirmationKey generateConfimationKey(String userId){
        ConfirmationKey key = new ConfirmationKey();
        key.setUserId(userId);
        key.setValid(true);
        key.setValidTill(LocalDateTime.now().plusDays(3));
        repository.insert(key);
        return key;
    }
    public ConfirmationKey getConfirmationKey(String keyId){
        return repository.getById(keyId).get();
    }
    public void invalidate(String keyId){
        repository.invalidate(keyId);
    }

}
