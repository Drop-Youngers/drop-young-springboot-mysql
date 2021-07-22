package com.java.main.springstarter.v1.repositories;

import com.java.main.springstarter.v1.enums.EFileStatus;
import com.java.main.springstarter.v1.fileHandling.File;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IFileRepository extends JpaRepository<File, UUID> {
    Page<File> findAllByStatus(Pageable pageable, EFileStatus status);

}
