package com.java.main.springstarter.v1.dtos;

import com.java.main.springstarter.v1.enums.EGender;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
public class UpdateUserDTO {
    @Email
    private String email;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Pattern(regexp = "[0-9]{12}")
    private String mobile;


    private EGender gender;

}