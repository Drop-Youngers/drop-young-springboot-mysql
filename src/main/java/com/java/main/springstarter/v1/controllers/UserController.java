package com.java.main.springstarter.v1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/users")
public class UserController {
    @Autowired
    private  UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    @Autowired
    private  JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserController(UserService userService,JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping(path = "/current")
    public ResponseEntity<ApiResponse> currentlyLoggedInUser(){
        return ResponseEntity.ok(new ApiResponse(true,"user found",userService.getLoggedInUser()));
    }

    @GetMapping
    public Page<User> getUsers(@RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
                               @RequestParam(value = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int size) {
        return userService.getAllUsers(page,size);
    }

    @GetMapping(path = "/{userID}")
    public ResponseEntity<ApiResponse> getUser(@ApiParam(value="Get user by id",required = true) @PathVariable("userID") UUID userID){
        return  ResponseEntity.ok(new ApiResponse(true,"user found",this.userService.getUser(userID)));
    }

    @PutMapping(path = "/{userID}")
    public ResponseEntity<ApiResponse> updateUser(@PathVariable("userID") UUID userID,@Valid @RequestBody UserUpdateDTO userdataRequest){

        userService.updateUser(userID,userdataRequest);

        User user = userService.getUser(userID);

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/users/{email}")
                .buildAndExpand(user.getEmail()).toUri();
        return ResponseEntity.created(location).body(new ApiResponse(true,"user  updated successfully",user));
    }

    @PutMapping(path = "/status/{userId}")
    public ResponseEntity<ApiResponse> updateUserStatus(@PathVariable UUID userId, @RequestBody UpdateStatusDTO statusDTO){
        User user = userService.updateUserStatus(userId,statusDTO.getStatus());
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/users/{userID}")
                .buildAndExpand(user.getId()).toUri();
        return ResponseEntity.created(location).body(new ApiResponse(true,"user  updated successfully",user));
    }


    @DeleteMapping(path = "/{userID}")
    public ResponseEntity<ApiResponse> deleteUser(@ApiParam(value="delete user",required = true) @PathVariable("userID") UUID userID){
        return ResponseEntity.ok(new ApiResponse(true,"user removed successfully",userService.deleteUser(userID)));
    }

    @GetMapping(path = "/role/{roleName}")
    public ResponseEntity<ApiResponse> getAllByRole(@RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
                                                    @RequestParam(value = "size", defaultValue = Constants.DEFAULT_PAGE_SIZE) int size,
                                                    @PathVariable ERoleType roleName){
        return ResponseEntity.ok(new ApiResponse(true,"User with role "+roleName+" found ",userService.findAllByRole(page,size,roleName)));
    }

    @PostMapping(path = "/profile/{userID}")
    public ResponseEntity<ApiResponse> updateProfileImage(@PathVariable UUID userID, @RequestParam("file") MultipartFile file){
        User user = userService.updateProfileImage(userID,file);
        return ResponseEntity.ok(new ApiResponse(true,"created",user));
    }

    @GetMapping(path = "/load-file/profile/{userID}")
    public ResponseEntity<InputStreamResource> loadProfileImage(@PathVariable UUID userID) throws IOException {
        User user = userService.getUser(userID);

        if(user.getImageUrl() == null){
            throw new AppException("Profile haven't been uploaded");
        }

        Path filePath = Paths.get(user.getImageUrl());
        return ResponseEntity.ok().contentLength(Files.size(filePath))
                .contentType(MediaType.parseMediaType(URLConnection.guessContentTypeFromName(filePath.toString())))
                .body(new InputStreamResource(Files.newInputStream(filePath, StandardOpenOption.READ)));
    }

    @GetMapping(path = "/search/{searchKey}")
    public List<User> searchUsers(@PathVariable String searchKey){
        return userService.searchUser(searchKey);
    }

    @PostMapping(path = "/register")
    @ApiOperation(value = "create new user", response = User.class)
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
