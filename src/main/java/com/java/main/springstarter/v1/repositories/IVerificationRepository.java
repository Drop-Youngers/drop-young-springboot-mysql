package com.java.main.springstarter.v1.repositories;

import com.java.main.springstarter.v1.models.Verification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface IVerificationRepository extends JpaRepository<Verification, UUID> {

    @Query("SELECT v FROM Verification v WHERE v.verificationCode=:verificationCode AND v.expiresAt > local_datetime")
    Optional<Verification> getVerificationByVerificationCodeAndExpiresAt(String verificationCode);

}
