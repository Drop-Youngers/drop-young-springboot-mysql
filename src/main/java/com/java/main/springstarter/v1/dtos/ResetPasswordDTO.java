package com.java.main.springstarter.v1.dtos;

import com.java.main.springstarter.v1.security.ValidPassword;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class ResetPasswordDTO {

    @NotBlank
    private String email;

    @NotBlank
    private String activationCode;

    @ValidPassword
    private String password;
}
