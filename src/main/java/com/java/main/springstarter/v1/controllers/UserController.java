package com.java.main.springstarter.v1.controllers;

import com.java.main.springstarter.v1.models.User;
import com.java.main.springstarter.v1.payload.ApiResponse;
import com.java.main.springstarter.v1.repositories.IRoleRepository;
import com.java.main.springstarter.v1.security.JwtTokenProvider;
import com.java.main.springstarter.v1.services.IUserService;
import com.java.main.springstarter.v1.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/users")
public class UserController {

    private  IUserService userService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private  JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserController(IUserService userService, BCryptPasswordEncoder bCryptPasswordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping(path = "/current-user")
    public ResponseEntity<ApiResponse> currentlyLoggedInUser(){
        return ResponseEntity.ok(new ApiResponse(true,  userService.getLoggedInUser()));
    }

    @GetMapping
    public List<User> getAllUsers() {
        return this.userService.getAll();
    }

    @GetMapping
    public Page<User> getAllUsers(@RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
                                  @RequestParam(value = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int limit
    ) {
        Pageable pageable = (Pageable) PageRequest.of(page, limit, Sort.Direction.ASC, "id");
        return userService.getAll(pageable);
    }

    @GetMapping(path="/{id}")
    public ResponseEntity<User> getById(@PathVariable(value = "id") UUID id) {
        return ResponseEntity.ok(this.userService.getById(id));
    }

    @PostMapping(path = "/register")
    public ResponseEntity<Response> register(@RequestBody @Valid SignUpDTO signUpRequest){

        User user = new User(signUpRequest.getEmail(),signUpRequest.getFirstName(),signUpRequest.getLastName(),signUpRequest.getMobile(),signUpRequest.getNationalId(),signUpRequest.getGender(),signUpRequest.getPassword());

        boolean userExists = userRepository.findByEmail(user.getEmail()).isPresent();
        if(user.getEmail() != null && userExists){
            throw new ApiRequestException("Email is already in use");
        }

        if(userRepository.existsByMobile(user.getMobile())){
            throw new ApiRequestException("Phone number already in use");
        }

        if(userRepository.existsByNationalId(user.getNationalId())){
            throw new ApiRequestException("NationalID number already in use");
        }

        String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());

        user.setPassword(encodedPassword);

        Role userRole = roleRepository.findByName(signUpRequest.getRole()).orElseThrow(()-> new ApiRequestException("User Role not set"));

        user.setRoles(Collections.singleton(userRole));
        userRepository.save(user);

        return new ResponseEntity<>(new Response("Registered successfully", ZonedDateTime.now(), true), HttpStatus.CREATED);
    }
}