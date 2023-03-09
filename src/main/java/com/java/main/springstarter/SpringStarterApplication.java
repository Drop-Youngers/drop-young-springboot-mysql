package com.java.main.springstarter;

import com.java.main.springstarter.v1.enums.ERole;
import com.java.main.springstarter.v1.models.Role;
import com.java.main.springstarter.v1.repositories.IRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@SpringBootApplication
public class SpringStarterApplication {

    private final IRoleRepository roleRepository;

    @Autowired
    public SpringStarterApplication(IRoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringStarterApplication.class, args);
    }

    @Bean
    public void registerRoles() {
        Set<ERole> roles = new HashSet<>();
        roles.add(ERole.ADMIN);
        roles.add(ERole.NORMAL);

        for (ERole role : roles) {
            Optional<Role> roleByName = roleRepository.findByName(role);
            if (!roleByName.isPresent()) {
                Role newRole = new Role(role, role.toString());
                roleRepository.save(newRole);
                System.out.println("Created: " + role.toString());

            }
        }
    }
}
