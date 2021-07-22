package com.java.main.springstarter.v1.services;

import com.java.main.springstarter.v1.dtos.UpdateUserDTO;
import com.java.main.springstarter.v1.enums.ERole;
import com.java.main.springstarter.v1.enums.EUserStatus;
import com.java.main.springstarter.v1.models.User;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public interface IUserService {
    public Page<User> getAllUsers(int page, int size);

    public User getUser(UUID id);

    public User getLoggedInUser();

    public Page<User> findAllByRole(int page, int size, ERole roleName);

    public List<User> searchUser(String searchKey);

    public User updateUser(UUID userID, UpdateUserDTO dto);

    public User updateUserStatus(@PathVariable UUID userId, EUserStatus userStatus);

    public User updateProfileImage(UUID userID, MultipartFile file);

    public User deleteUser(UUID id);


}