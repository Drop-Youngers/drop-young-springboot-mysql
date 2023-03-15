package com.java.main.springstarter.v1.serviceImpls;

import com.java.main.springstarter.v1.enums.ERole;
import com.java.main.springstarter.v1.enums.EUserStatus;
import com.java.main.springstarter.v1.exceptions.BadRequestException;
import com.java.main.springstarter.v1.exceptions.ResourceNotFoundException;
import com.java.main.springstarter.v1.fileHandling.File;
import com.java.main.springstarter.v1.models.User;
import com.java.main.springstarter.v1.models.Verification;
import com.java.main.springstarter.v1.repositories.IUserRepository;
import com.java.main.springstarter.v1.repositories.IVerificationRepository;
import com.java.main.springstarter.v1.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements IUserService {

    private final IUserRepository userRepository;
    private final IVerificationRepository verificationRepository;

    @Autowired
    public UserServiceImpl(IUserRepository userRepository, IVerificationRepository verificationRepository) {
        this.userRepository = userRepository;
        this.verificationRepository = verificationRepository;
    }

    @Override
    public List<User> getAll() {
        return this.userRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    @Override
    public Page<User> getAll(Pageable pageable) {
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
        User entity = this.userRepository.save(user);
        Verification verification = new Verification();
        verification.setUser(entity);
        entity.setVerification(verification);
        this.verificationRepository.save(verification);
        return entity;
    }

    @Override
    public User update(UUID id, User user) {
        User entity = this.userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id.toString()));

        Optional<User> userOptional = this.userRepository.findByEmail(user.getEmail());
        if (userOptional.isPresent() && (userOptional.get().getId() != entity.getId()))
            throw new BadRequestException(String.format("User with email '%s' already exists", entity.getEmail()));

        if (!user.getEmail().equals(entity.getEmail())) {
            Verification verification = this.verificationRepository.getById(entity.getVerification().getId());
            verification.setVerifiedAt(null);
            verification.setVerified(false);
            verification.setVerificationCode(null);
            verification.setExpiresAt(null);
            this.verificationRepository.save(verification);
        }

        entity.setEmail(user.getEmail());
        entity.setFirstName(user.getFirstName());
        entity.setLastName(user.getLastName());
        entity.setMobile(user.getMobile());
        entity.setGender(user.getGender());

        return this.userRepository.save(entity);
    }

    @Override
    public boolean delete(UUID id) {
        this.userRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("User", "id", id));

        this.userRepository.deleteById(id);
        return true;
    }

    @Override
    public List<User> getAllByRole(ERole role) {
        return this.userRepository.findByRoles(role);
    }

    @Override
    public Page<User> getAllByRole(Pageable pageable, ERole role) {
        return this.userRepository.findByRoles(pageable, role);
    }

    @Override
    public List<User> searchUser(String searchKey) {
        return this.userRepository.searchUser(searchKey);
    }

    @Override
    public Page<User> searchUser(Pageable pageable, String searchKey) {
        return this.userRepository.searchUser(pageable, searchKey);
    }

    @Override
    public User getLoggedInUser() {
        String email;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }

        return userRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", email));
    }

    @Override
    public User getByEmail(String email) {
        return this.userRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", email));
    }


    @Override
    public User changeStatus(UUID id, EUserStatus status) {
        User entity = this.userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id.toString()));

        entity.setStatus(status);

        return this.userRepository.save(entity);
    }

    @Override
    public User changeProfileImage(UUID id, File file) {
        User entity = this.userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Document", "id", id.toString()));

        entity.setProfileImage(file);
        return this.userRepository.save(entity);

    }
}
