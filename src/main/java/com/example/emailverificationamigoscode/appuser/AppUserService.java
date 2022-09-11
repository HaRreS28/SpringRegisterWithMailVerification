package com.example.emailverificationamigoscode.appuser;

import com.example.emailverificationamigoscode.registration.token.ConfirmationToken;

import com.example.emailverificationamigoscode.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {
    private final static String USER_NOT_FOUND_MSG = "user with email %s not found";
    private final PasswordEncoder passwordEncoder;
    private final AppUserRepository appUserRepository;
    private final ConfirmationTokenService confirmationTokenService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return appUserRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email)));
    }

    @Transactional
    public String singUpUser(AppUser appUser) {
        Optional<AppUser> optionalAppUser = appUserRepository
                .findByEmail(appUser.getEmail());

        boolean userExists = optionalAppUser.isPresent();
        boolean isFirstToken=true;

        if (userExists) {
            if(optionalAppUser.get().getEnabled()){

                throw new IllegalStateException("email already taken");
            }
            else isFirstToken=false;
        }

        if(isFirstToken) {
            String encodedPassword = passwordEncoder.encode(appUser.getPassword());
            appUser.setPassword(encodedPassword);
            appUserRepository.save(appUser);
        }
        else{
            appUser=optionalAppUser.get();
        }
        //TODO: Send confirmation token
       String token=confirmationTokenService.createToken();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                appUser
        );

        confirmationTokenService.saveConfirmationToken(confirmationToken);

        //TODO:SEND EMAIL
        return token;
    }

    @Transactional
    public void enableAppUser(String email) {
        appUserRepository.enableAppUser(email);
    }
}
