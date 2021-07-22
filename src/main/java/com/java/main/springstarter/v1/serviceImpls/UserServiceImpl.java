package com.java.main.springstarter.v1.serviceImpls;

import com.java.main.springstarter.v1.dtos.UpdateUserDTO;
import com.java.main.springstarter.v1.enums.ERole;
import com.java.main.springstarter.v1.enums.EUserStatus;
import com.java.main.springstarter.v1.exceptions.AppException;
import com.java.main.springstarter.v1.exceptions.BadRequestException;
import com.java.main.springstarter.v1.exceptions.ResourceNotFoundException;
import com.java.main.springstarter.v1.fileHandling.File;
import com.java.main.springstarter.v1.models.User;
import com.java.main.springstarter.v1.repositories.IRoleRepository;
import com.java.main.springstarter.v1.repositories.IUserRepository;
import com.java.main.springstarter.v1.services.IUserService;
import com.java.main.springstarter.v1.utils.ConstantsUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public class UserServiceImpl implements IUserService {

    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;

    @Value("${upload.directory.profile}")
    private String profileImageDirectory;

    @Autowired
    public UserServiceImpl(IUserRepository userRepository, IRoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public List<User> getAll() {
        return this.userRepository.findAll();
    }

    @Override
    public Page<User> getAllPaginated(Pageable pageable) {
        return this.userRepository.findAll(pageable);
    }

    @Override
    public User getById(UUID id) {
        return this.userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id.toString()));
    }

    @Override
    public User create(User user) {
        Optional<User> userOptional = this.userRepository.findByEmail(user.getEmail());
        if (userOptional.isPresent())
            throw new BadRequestException(String.format("User with email '%s' already exists", user.getEmail()));

        return this.userRepository.save(user);
    }

    @Override
    public User update(UUID id, User user) {
        User entity = this.userRepository.findById(id).orElseThrow(
                () ->  new ResourceNotFoundException("User", "id", id.toString()));

        Optional<User> userOptional = this.userRepository.findByEmail(user.getEmail());
        if (userOptional.isPresent() && (userOptional.get().getId() != entity.getId()))
            throw new BadRequestException(String.format("User with email '%s' already exists", entity.getEmail()));

        entity.setEmail(user.getEmail());
        entity.setFirstName(user.getFirstName());
        entity.setLastName(user.getLastName());
        entity.setMobile(user.getMobile());
        entity.setGender(user.getGender());

        return this.userRepository.save(entity);
    }

    @Override
    public boolean delete(UUID id) {
        User user = this.userRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("User", "id", id));

        this.userRepository.deleteById(id);
        return true;
    }

    @Override
    public List<User> getAllByRole(ERole role) {
        return null;
    }

    @Override
    public Page<User> getAllByRolePaginated(Pageable pageable, ERole role) {
        return null;
    }

    @Override
    public List<User> searchUser(String searchKey) {
        return null;
    }

    @Override
    public Page<User> searchUserPaginated(String searchKey) {
        return null;
    }

    @Override
    public User getLoggedInUser() {
        return null;
    }

    @Override
    public User changeStatus(UUID id, EUserStatus status) {
        return null;
    }

    @Override
    public User changeProfileImage(UUID userID, File file) {
        return null;
    }
}
