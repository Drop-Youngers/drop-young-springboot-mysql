package com.java.main.springstarter.repositories;

import com.java.main.springstarter.models.Role;
import com.java.main.springstarter.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IUserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailOrMobile(String email, String mobile);
    Optional<User> findById(UUID userID);
    Boolean existsByMobile(String mobile);
    Page<User> findByRolesIn(Pageable pageable, List<Role> roleList);

    @Query("SELECT u FROM User u" +
            " WHERE (lower(u.firstName)  LIKE ('%' || lower(:searchKey) || '%')) " +
            " OR (lower(u.lastName) LIKE ('%' || lower(:searchKey) || '%')) " +
            " OR (lower(u.email) LIKE ('%' || lower(:searchKey) || '%'))")
    List<User> searchUser(String searchKey);
}
