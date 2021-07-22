package com.java.main.springstarter.v1.services;


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
    public User getUser(UUID id);
    public User deleteUser(UUID id);

    public Page<User> getAllUsers(int page, int size);

    public User getLoggedInUser();

    public User updateUser(UUID userID, UserUpdateDTO userdataRequest);

    public User updateUserStatus(@PathVariable UUID userId, EUserStatus userStatus);

    public Page<User> findAllByRole(int page, int size, ERole roleName);

    public User updateProfileImage(UUID userID, MultipartFile file);

    public List<User> searchUser(String searchKey);
}