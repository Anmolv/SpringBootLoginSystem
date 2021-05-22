package com.courses.login.registration.token;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ConfirmationTokenService {
    @Autowired private ConfirmationTokenRepository confirmationTokenRepository;

    public void saveConfirmationToken(ConfirmationToken token){
        confirmationTokenRepository.save(token);
    }

    public Optional<ConfirmationToken> getToken(String token){
        return confirmationTokenRepository.findByToken(token);
    }

    public void setConfirmedAt(String token){
        ConfirmationToken confirmationToken = getToken(token)
                .orElseThrow(() -> new IllegalArgumentException("token not found"));
        confirmationToken.setConfirmedAt(LocalDateTime.now());
        confirmationTokenRepository.save(confirmationToken);
    }
}
