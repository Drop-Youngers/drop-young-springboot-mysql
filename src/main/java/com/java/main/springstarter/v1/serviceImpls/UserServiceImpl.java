package com.java.main.springstarter.v1.serviceImpls;

import com.java.main.springstarter.v1.dtos.UpdateUserDTO;
import com.java.main.springstarter.v1.enums.ERole;
import com.java.main.springstarter.v1.enums.EUserStatus;
import com.java.main.springstarter.v1.models.User;
import com.java.main.springstarter.v1.repositories.IRoleRepository;
import com.java.main.springstarter.v1.repositories.IUserRepository;
import com.java.main.springstarter.v1.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;


public class UserServiceImpl implements IUserService {

    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;

    @Value("${upload.directory.profile}")
    private String profileImageDirectory;

    @Autowired
    public UserService(IUserRepository userRepository, IRoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public Page<User> getAllUsers(int page, int size) {
        Constants.validatePageNumberAndSize(page,size);
        Pageable pageable = (Pageable) PageRequest.of(page,size, Sort.Direction.ASC,"firstName");
        Page<User> users = userRepository.findAll(pageable);
        return users;
    }


    public User getUser(UUID id) {
        return this.userRepository.findById(userID).orElseThrow(()-> new ApiRequestException("User with id "+userID+" not found"));
    }

    public User deleteUser(UUID userID) {
        User user = userRepository.findById(userID).orElseThrow(() -> new AppException("user with id "+userID+ " not found"));
        this.userRepository.deleteById(userID);
        return user;
    }




    public User getLoggedInUser(){
        String email;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principal instanceof UserDetails){
            email = ((UserDetails) principal).getUsername();
        }else{
            email = principal.toString();
        }
        User findByEmail = userRepository.findByEmail(email).orElseThrow(() -> new ApiRequestException("User not found"));

        return findByEmail;
    }

    @Override
    public Page<User> findAllByRole(int page, int size, ERole roleName) {
        return null;
    }

    public User updateUser(UUID userID, UserUpdateDTO userdataRequest) {
        User user = userRepository.findById(userID).orElseThrow(()-> new ResourceNotFoundException("Get user by id", ""+ userID,new User()));

        if(getLoggedInUser().getId() != user.getId())
            throw new AppException("You are not authorized to update user");

        if(userRepository.existsByMobile(userdataRequest.getMobile()) && !(userdataRequest.getMobile().equalsIgnoreCase(user.getMobile()))){
            throw  new AppException("Phone number already in use.");
        }else {
            user.setMobile(userdataRequest.getMobile());
        }

        if(userRepository.existsByNationalId(userdataRequest.getNationalId()) && !(userdataRequest.getNationalId().equalsIgnoreCase(user.getNationalId()))){
            throw  new AppException("NationalID number already in use.");
        }else {
            user.setNationalId(userdataRequest.getNationalId());
        }

        user.setFirstName(userdataRequest.getFirstName());
        user.setLastName(userdataRequest.getLastName());
        user.setGender(userdataRequest.getGender());
        user.setEmail(userdataRequest.getEmail());


        userRepository.save(user);

        return user;
    }


    public User updateUserStatus(@PathVariable UUID userId, EUserStatus userStatus){

        User user = userRepository.findById(userId).orElseThrow(() -> new AppException("User with id "+userId+" is not found"));

        if(getLoggedInUser().getRoles().contains(ERoleType.ADMIN))
            throw new AppException("Not authorized to update user status");

        user.setStatus(userStatus);

        userRepository.save(user);

        return user;
    }

    @Override
    public User updateProfileImage(UUID userID, MultipartFile file) {
        return null;
    }

    public Page<User> findAllByRole(int page,int size,ERoleType roleName) {
        Role role = roleRepository.findByName(roleName).orElseThrow(() -> new EntityNotFoundException(Role.class,"name",roleName+""));
        Pageable pageable = (Pageable) PageRequest.of(page,size,Sort.Direction.DESC,"id");
        List<Role> roleList = new ArrayList<>();
        roleList.add(role);
        return userRepository.findByRolesIn(pageable,roleList);
    }

    public User updateProfileImage(UUID userID, MultipartFile file) {
        User user = userRepository.findById(userID).orElseThrow(()-> new ApiRequestException("User with id "+userID+" is not found"));

        String fileName = FileUtil.generateUUID(Objects.requireNonNull(file.getOriginalFilename()));

        user.setImageUrl(fileService.save(file, profileImageDirectory, fileName));

        return userRepository.save(user);
    }

    public List<User> searchUser(String searchKey) {
        return userRepository.searchUser(searchKey);
    }

    @Override
    public User updateUser(UUID userID, UpdateUserDTO dto) {
        return null;
    }

    @Override
    public User updateUserStatus(UUID userId, EUserStatus userStatus) {
        return null;
    }
}
