package com.java.main.springstarter.v1.models;

import com.java.main.springstarter.v1.fileHandling.File;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "verifications", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id"})})
public class Verification {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne()
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "is_verified")
    private boolean isVerified = false;

    @Column(name = "verification_code")
    private String verificationCode;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;
}
