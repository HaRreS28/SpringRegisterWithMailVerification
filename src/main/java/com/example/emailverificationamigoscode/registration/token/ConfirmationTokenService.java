package com.example.emailverificationamigoscode.registration.token;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ConfirmationTokenService {
    private final ConfirmationTokenRepository tokenRepository;

    public void saveConfirmationToken(ConfirmationToken token){
        tokenRepository.save(token);
    }

    public String createToken() {
        String token = UUID.randomUUID().toString();
        Optional<ConfirmationToken> byToken = tokenRepository.findByToken(token);
        while (byToken.isPresent()) {
            token = UUID.randomUUID().toString();
            byToken = tokenRepository.findByToken(token);
        }
        return token;
    }

   public Optional<ConfirmationToken>getToken(String token){
        return tokenRepository.findByToken(token);
    }

    @Transactional
    public void setConfirmedAt(String token) {
        tokenRepository.updateConfirmedAt(token, LocalDateTime.now());
    }
}
