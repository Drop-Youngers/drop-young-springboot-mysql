package com.java.main.springstarter.dtos;


import com.java.main.springstarter.enums.EGender;
import com.java.main.springstarter.enums.ERole;
import com.java.main.springstarter.security.ValidPassword;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class SignUpDTO {

    @Email
    private  String email;

    @NotBlank
    private  String firstName;

    @NotBlank
    private  String lastName;

    @NotBlank
    private  String mobile;

    private EGender gender;

    private ERole role;

    @ValidPassword
    private  String password;
}
