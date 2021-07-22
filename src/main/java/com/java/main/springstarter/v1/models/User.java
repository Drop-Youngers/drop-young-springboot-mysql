package com.java.main.springstarter.v1.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.java.main.springstarter.v1.audits.TimestampAudit;
import com.java.main.springstarter.v1.enums.EGender;
import com.java.main.springstarter.v1.enums.EUserStatus;
import com.java.main.springstarter.v1.fileHandling.File;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;



@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="users", uniqueConstraints = { @UniqueConstraint(columnNames={ "email" }), @UniqueConstraint(columnNames={ "mobile" })})
public class User extends TimestampAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private UUID id;


    @NotBlank
    @Column(name="email")
    private String email;

    @Column(name="first_name")
    private String firstName;

    @Column(name="last_name")
    private String lastName;

    @Column(name="mobile")
    private String mobile;

    @JsonIgnore
    @NotBlank
    @Column(name="password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name="gendder")
    private EGender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EUserStatus status = EUserStatus.PENDING;

    @JoinColumn(name="profile_image_id")
    @OneToOne(cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private File profileImage;


    @Column(name="activation_code")
    private String activationCode;



    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public User(String email, String firstName, String lastName, String mobile, EGender gender, String password) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobile = mobile;
        this.gender = gender;
        this.password = password;
    }
}
