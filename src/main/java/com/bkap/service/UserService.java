package com.bkap.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;
import com.bkap.entity.User;
import com.bkap.repository.UserRepository;

@Service
public class UserService implements UserDetailsService{

    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    private UserRepository userRepository;

    UserService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override    
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        com.bkap.entity.User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("Email Not Found: "+ email)) ;
        
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        String role = user.getRole();
        if (role != null && !role.isEmpty()) {
            String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        } else {
            throw new UsernameNotFoundException("Emai has no role " + email);
        }
        
        return new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            authorities
        );
    
    }

    public void registerUser(User user){
        if (userRepository.findByEmail(user.getEmail()).isPresent()){
            throw new RuntimeException("Email already exists "+ user.getEmail());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("ROLE_USER");
        }
        user.setStatus(1); // default active
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
}
