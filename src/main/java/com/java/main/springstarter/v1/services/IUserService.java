package com.java.main.springstarter.v1.services;

import com.java.main.springstarter.v1.dtos.UpdateUserDTO;
import com.java.main.springstarter.v1.enums.ERole;
import com.java.main.springstarter.v1.enums.EUserStatus;
import com.java.main.springstarter.v1.fileHandling.File;
import com.java.main.springstarter.v1.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public interface IUserService {


    public List<User> getAll();

    public Page<User> getAllPaginated(Pageable pageable);

    public User getById(UUID id);

    public User create(User user);

    public User update(UUID id, User user);

    public boolean delete(UUID id);

    public List<User> getAllByRole(ERole role);

    public Page<User> getAllByRolePaginated(Pageable pageable, ERole role);

    public List<User> searchUser(String searchKey);

    public Page<User> searchUserPaginated(Pageable pageable, String searchKey);

    public User getLoggedInUser();

    public User changeStatus(UUID id, EUserStatus status);

    public User changeProfileImage(UUID id, File file);

}