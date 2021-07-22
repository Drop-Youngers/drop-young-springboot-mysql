package com.java.main.springstarter.v1.dtos;

import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
public class InitiatePasswordDTO {

    @NotBlank
    @Email
    private String email;
}
