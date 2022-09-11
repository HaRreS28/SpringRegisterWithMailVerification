package com.example.emailverificationamigoscode.registration;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.Email;


@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class RegistrationRequest {
        private String firstName;
        private String lastName;
        @Email
        private String email;
        private String password;

}