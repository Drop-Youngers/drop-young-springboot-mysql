package com.java.main.springstarter.v1.dtos;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
public class SignInDTO {

    @NotBlank
    @Email
    private  String email;

    @NotBlank
    private  String password;
}

