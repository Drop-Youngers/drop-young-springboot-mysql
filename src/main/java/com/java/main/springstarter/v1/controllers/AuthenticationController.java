package com.java.main.springstarter.v1.controllers;

import com.java.main.springstarter.v1.dtos.InitiatePasswordDTO;
import com.java.main.springstarter.v1.dtos.InitiateVerificationDTO;
import com.java.main.springstarter.v1.dtos.ResetPasswordDTO;
import com.java.main.springstarter.v1.dtos.SignInDTO;
import com.java.main.springstarter.v1.enums.EUserStatus;
import com.java.main.springstarter.v1.exceptions.AppException;
import com.java.main.springstarter.v1.models.User;
import com.java.main.springstarter.v1.models.Verification;
import com.java.main.springstarter.v1.payload.ApiResponse;
import com.java.main.springstarter.v1.payload.JwtAuthenticationResponse;
import com.java.main.springstarter.v1.repositories.IUserRepository;
import com.java.main.springstarter.v1.repositories.IVerificationRepository;
import com.java.main.springstarter.v1.security.JwtTokenProvider;
import com.java.main.springstarter.v1.services.IUserService;
import com.java.main.springstarter.v1.services.MailService;
import com.java.main.springstarter.v1.utils.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/v1/auth")
public class AuthenticationController {

    private final IUserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MailService mailService;
    private final IUserRepository userRepository;
    private final IVerificationRepository verificationRepository;

    @Autowired
    public AuthenticationController(IUserService userService, AuthenticationManager authenticationManager,
                                    JwtTokenProvider jwtTokenProvider, MailService mailService,
                                    BCryptPasswordEncoder bCryptPasswordEncoder, IUserRepository userRepository, IVerificationRepository verificationRepository) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.mailService = mailService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
        this.verificationRepository = verificationRepository;
    }


    @PostMapping(path = "/signin")
    public ResponseEntity<JwtAuthenticationResponse> signin(@Valid @RequestBody SignInDTO dto) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = null;

        try {
            jwt = jwtTokenProvider.generateToken(authentication);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    @PostMapping(path = "/initiate-reset-password")
    public ResponseEntity<ApiResponse> initiateResetPassword(@RequestBody @Valid InitiatePasswordDTO dto) {
        User user = this.userService.getByEmail(dto.getEmail());
        user.setActivationCode(Utility.randomUUID(6, 0, 'N'));
        user.setStatus(EUserStatus.RESET);

        this.userService.create(user);

        mailService.sendResetPasswordMail(user.getEmail(), user.getFirstName() + " " + user.getLastName(), user.getActivationCode());

        return ResponseEntity.ok(new ApiResponse(true, "Please check your mail and activate account"));
    }


    @PostMapping(path = "/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@RequestBody @Valid ResetPasswordDTO dto) {
        User user = this.userService.getByEmail(dto.getEmail());

        if (Utility.isCodeValid(user.getActivationCode(), dto.getActivationCode()) &&
                (user.getStatus().equals(EUserStatus.RESET)) || user.getStatus().equals(EUserStatus.PENDING)) {
            user.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
            user.setActivationCode(Utility.randomUUID(6, 0, 'N'));
            user.setStatus(EUserStatus.ACTIVE);
            this.userRepository.save(user);
        } else {
            throw new AppException("Invalid code or account status");
        }
        return ResponseEntity.ok(new ApiResponse(true, "Password successfully reset"));
    }

    @PostMapping("/initiate-email-verification")
    public ResponseEntity<ApiResponse> initiateEmailVerification(@RequestBody InitiateVerificationDTO dto) {
        User user = this.userService.getByEmail(dto.getEmail());
        System.out.println(user.toString());
        Verification verification = new Verification();
        String verificationCode = Utility.randomUUID(6, 1, 'N');
        verification.setUser(user);
        verification.setVerificationCode(verificationCode);
        verification.setExpiresAt(LocalDateTime.now().plusHours(5));

        this.verificationRepository.save(verification);

        user.setVerification(verification);

        this.userService.create(user);

        mailService.sendVerificationMail(user.getEmail(), user.getFirstName() + user.getLastName(), verificationCode);
        return ResponseEntity.ok(new ApiResponse(true, "Email verification code sent successfully"));
    }

    @PutMapping("/verify-email/{verificationCode}")
    public ResponseEntity<ApiResponse> verifyEmail(@PathVariable(name = "verificationCode") String verificationCode) {
        Optional<Verification> _verification = this.verificationRepository.getVerificationByVerificationCodeAndExpiresAt(verificationCode);
        if (_verification.isEmpty()) return ResponseEntity.ok(new ApiResponse(false, "Verification token expired"));
        Verification verification = _verification.get();
        verification.setVerificationCode(null);
        verification.setExpiresAt(null);
        verification.setVerified(true);
        verification.setVerifiedAt(LocalDateTime.now());
        this.verificationRepository.save(verification);
        return ResponseEntity.ok(new ApiResponse(true, "Email verified successfully"));
    }
}