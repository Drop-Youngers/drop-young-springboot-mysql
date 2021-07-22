package com.java.main.springstarter.v1.repositories;

import com.java.main.springstarter.v1.enums.ERole;
import com.java.main.springstarter.v1.models.Role;
import com.java.main.springstarter.v1.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IRoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByName(ERole role);
}
