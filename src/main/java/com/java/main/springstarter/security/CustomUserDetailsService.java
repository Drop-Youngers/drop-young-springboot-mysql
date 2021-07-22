package com.java.main.springstarter.security;

import com.java.main.springstarter.models.User;
import com.java.main.springstarter.repositories.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.UUID;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private IUserRepository userRepository;

    @Transactional
    public UserDetails loadByUserId(UUID id) {
        User user = this.userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found with id: "+id));
        return UserPrincipal.create(user);
    }

    @Transactional
    public UserDetails loadUserByUsername(String s) {
        User user = userRepository.findByEmailOrMobile(s,s).orElseThrow(()-> new UsernameNotFoundException("user not found with email or mobile of "+s));
        return UserPrincipal.create(user);
    }
}
