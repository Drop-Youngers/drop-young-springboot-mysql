package com.java.main.springstarter.v1.controllers;

import com.java.main.springstarter.v1.dtos.SignUpDTO;
import com.java.main.springstarter.v1.exceptions.BadRequestException;
import com.java.main.springstarter.v1.fileHandling.File;
import com.java.main.springstarter.v1.fileHandling.FileStorageService;
import com.java.main.springstarter.v1.models.Role;
import com.java.main.springstarter.v1.models.User;
import com.java.main.springstarter.v1.payload.ApiResponse;
import com.java.main.springstarter.v1.repositories.IRoleRepository;
import com.java.main.springstarter.v1.security.JwtTokenProvider;
import com.java.main.springstarter.v1.services.IFileService;
import com.java.main.springstarter.v1.services.IUserService;
import com.java.main.springstarter.v1.utils.Constants;
import org.apache.catalina.Cluster;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/users")
public class UserController {

    private final IUserService userService;
    private static final ModelMapper modelMapper = new ModelMapper();
    private final IRoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final FileStorageService fileStorageService;
    private final IFileService fileService;

    @Value("${uploads.directory.user_profiles}")
    private String directory;

    @Autowired
    public UserController(IUserService userService, IRoleRepository roleRepository,
                          BCryptPasswordEncoder bCryptPasswordEncoder, JwtTokenProvider jwtTokenProvider,
                          FileStorageService fileStorageService, IFileService fileService) {
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.fileService = fileService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping(path = "/current-user")
    public ResponseEntity<ApiResponse> currentlyLoggedInUser(){
        return ResponseEntity.ok(new ApiResponse(true,  userService.getLoggedInUser()));
    }

    @GetMapping
    public List<User> getAllUsers() {
        return this.userService.getAll();
    }

    @GetMapping(path="/paginated")
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
    public ResponseEntity<ApiResponse> register(@RequestBody @Valid SignUpDTO dto){

        User user = new User();

        String encodedPassword = bCryptPasswordEncoder.encode(dto.getPassword());
        Role role = roleRepository.findByName(dto.getRole()).orElseThrow(
                ()-> new BadRequestException("User Role not set"));

        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setGender(dto.getGender());
        user.setMobile(dto.getMobile());
        user.setPassword(encodedPassword);
        user.setRoles(Collections.singleton(role));

        User entity = this.userService.create(user);

        return ResponseEntity.ok(new ApiResponse(true, entity));
    }

    @PutMapping(path="/{id}/upload-profile")
    public ResponseEntity<ApiResponse> uploadProfileImage(
            @PathVariable(value = "id") UUID id,
            @RequestParam("file") MultipartFile document
    ) {
        this.userService.getById(id);
        File file = this.fileService.create(document, directory);

        User updated = this.userService.changeProfileImage(id, file);

        return ResponseEntity.ok(new ApiResponse(true, "File saved successfully", updated));

    }

    @GetMapping("/load-file/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> loadProfileImage(@PathVariable String filename) {

        Resource file = this.fileStorageService.load(directory, filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    private User convertDTO(SignUpDTO dto) {
        return modelMapper.map(dto, User.class);
    }
}