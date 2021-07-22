package com.java.main.springstarter.v1.repositories;

import com.java.main.springstarter.v1.enums.ERole;
import com.java.main.springstarter.v1.models.Role;
import com.java.main.springstarter.v1.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IUserRepository extends JpaRepository<User, UUID> {

    Optional<User> findById(UUID userID);

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailOrMobile(String email, String mobile);

    List<User> findByRoles(ERole role);
    Page<User> findByRoles(Pageable pageable, ERole role);

    @Query("SELECT u FROM User u" +
            " WHERE (lower(u.firstName)  LIKE ('%' || lower(:searchKey) || '%')) " +
            " OR (lower(u.lastName) LIKE ('%' || lower(:searchKey) || '%')) " +
            " OR (lower(u.email) LIKE ('%' || lower(:searchKey) || '%'))")
    List<User> searchUser(String searchKey);
    @Query("SELECT u FROM User u" +
            " WHERE (lower(u.firstName)  LIKE ('%' || lower(:searchKey) || '%')) " +
            " OR (lower(u.lastName) LIKE ('%' || lower(:searchKey) || '%')) " +
            " OR (lower(u.email) LIKE ('%' || lower(:searchKey) || '%'))")
    Page<User> searchUser(Pageable pageable, String searchKey);
}
