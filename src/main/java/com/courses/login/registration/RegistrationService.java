package com.courses.login.registration;

import com.courses.login.appuser.AppUser;
import com.courses.login.appuser.AppUserRole;
import com.courses.login.appuser.AppUserService;
import com.courses.login.email.EmailService;
import com.courses.login.registration.token.ConfirmationToken;
import com.courses.login.registration.token.ConfirmationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class RegistrationService {

    @Autowired private AppUserService appUserService;
    @Autowired private EmailValidator emailValidator;
    @Autowired private ConfirmationTokenService confirmationTokenService;
    @Autowired private EmailService emailService;

    public RegistrationService() {
    }

    public String register(RegistrationRequest request) {
        boolean isVaildEmail = emailValidator.test(request.getEmail());
        if(!isVaildEmail){
            throw new IllegalStateException("email not valid");
        }
        String token = appUserService.signUpUser(new AppUser(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPassword(),
                AppUserRole.USER
        ));
        String link = "http://localhost:8081/api/v1/registration/confirm?token=" + token;

        emailService.send(request.getEmail(), "Confirm Token: " + link);

        return token;
    }

    @Transactional
    public String confirmToken(String token){
        ConfirmationToken confirmationToken = confirmationTokenService.getToken(token)
                .orElseThrow(() -> new IllegalArgumentException("token not found"));

        if(confirmationToken.getConfirmedAt() != null){
            throw new IllegalStateException("token already confirmed");
        }

        LocalDateTime expiresAt = confirmationToken.getExpiredAt();
        if(LocalDateTime.now().isAfter(expiresAt)){
            throw new IllegalStateException("token expired");
        }

        confirmationTokenService.setConfirmedAt(token);
        appUserService.enableAppUser(
                confirmationToken.getAppUser().getEmail()
        );

        return "confirmed";
    }
}
