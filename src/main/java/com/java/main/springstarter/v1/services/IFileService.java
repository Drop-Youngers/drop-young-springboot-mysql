package com.java.main.springstarter.v1.services;

import com.java.main.springstarter.v1.enums.EFileSizeType;
import com.java.main.springstarter.v1.enums.EFileStatus;
import com.java.main.springstarter.v1.exceptions.InvalidFileException;
import com.java.main.springstarter.v1.exceptions.ResourceNotFoundException;
import com.java.main.springstarter.v1.fileHandling.File;
import com.java.main.springstarter.v1.fileHandling.FileStorageService;
import com.java.main.springstarter.v1.repositories.IFileRepository;
import com.java.main.springstarter.v1.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public interface IFileService {

        public List<File> getAll();

        public Page<File> getAll(Pageable pageable);

        public File getById(UUID id);

        public File create(MultipartFile document, String directory);

        public boolean delete(UUID id);

        public Page<File> getAllByStatus(Pageable pageable, EFileStatus status);

        public File uploadFile(MultipartFile file, String directory, UUID appointeeID) throws InvalidFileException, IOException;

        public String getFileExtension(String fileName);

        public String handleFileName(String fileName, UUID id) throws InvalidFileException;

        public boolean isValidExtension(String fileName) throws InvalidFileException;
}
