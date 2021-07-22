package com.java.main.springstarter.v1.controllers;

import com.java.main.springstarter.v1.dtos.InitiatePasswordDTO;
import com.java.main.springstarter.v1.dtos.SignInDTO;
import com.java.main.springstarter.v1.enums.EUserStatus;
import com.java.main.springstarter.v1.models.User;
import com.java.main.springstarter.v1.payload.ApiResponse;
import com.java.main.springstarter.v1.payload.JwtAuthenticationResponse;
import com.java.main.springstarter.v1.security.JwtTokenProvider;
import com.java.main.springstarter.v1.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/api/v1/auth")
public class AuthenticationController {

    private IUserService userService;
    private AuthenticationManager authenticationManager;
    private JwtTokenProvider jwtTokenProvider;


    @Autowired
    public AuthenticationController(IUserService userService, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }


    @PostMapping(path = "/signin")
    public ResponseEntity<JwtAuthenticationResponse> signin(@Valid @RequestBody SignInDTO dto){

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = null;

        try{
            jwt = jwtTokenProvider.generateToken(authentication);
        }catch (Exception e){
            e.printStackTrace();
        }

        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    @PostMapping(path = "/initiate-reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@RequestBody @Valid InitiatePasswordDTO dto){
        User user = this.userService.getByEmail(dto.getEmail());
        user.setActivationCode(Utility.randomUUID(6,0,'N'));
        user.setStatus(EUserStatus.RESET);

        userRepository.save(user);

        mailService.sendResetPasswordMail(user.getEmail(),user.getFirstName()+" "+user.getLastName(),user.getActivationCode());

        return ResponseEntity.ok(new ApiResponse(true, "Successfully registered, please check your mail and activate account"));
    }


    @PostMapping(path = "/reset-password")
    @ApiOperation(value = "confirm your password")
    public ResponseEntity<ApiResponse> confirmPassword(@RequestBody @Valid ResetPasswordDTO resetPasswordDTO){
        User user = userRepository.findByEmail(resetPasswordDTO.getEmail()).orElseThrow(() -> new EntityNotFoundException(User.class,"email",resetPasswordDTO.getEmail()));
        if(Utility.isCodeValid(user.getActivationCode(),resetPasswordDTO.getActivationCode()) && (user.getStatus().equals(EUserStatus.RESET)) || user.getStatus().equals(EUserStatus.PENDING)){
            user.setPassword(bCryptPasswordEncoder.encode(resetPasswordDTO.getPassword()));
            user.setActivationCode(Utility.randomUUID(6, 0, 'N'));
            user.setStatus(EUserStatus.ACTIVE);
            userRepository.save(user);
        }else{
            throw new AppException("Invalid code or account has status of pending");
        }
        return ResponseEntity.ok(new ApiResponse(true,"user successfully activated"));
    }