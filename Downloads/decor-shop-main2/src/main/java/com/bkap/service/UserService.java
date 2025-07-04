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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.stereotype.Service;

import com.bkap.dto.RegisterUserDto;
import com.bkap.entity.User;
import com.bkap.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    UserService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        com.bkap.entity.User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email Not Found: " + email));
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        String role = user.getRole();
        if (role != null && !role.isEmpty()) {
            String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;
            authorities.add(new SimpleGrantedAuthority(authority));
        } else {
            throw new UsernameNotFoundException("Email has no role " + email);
        }
        return new CustomUserDetails(user, authorities);
    }

    public void registerUser(RegisterUserDto dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists " + dto.getEmail());
        }

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());
        user.setRole("ROLE_USER"); // gán mặc định
        user.setStatus(1); // active
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    public Optional<User> findByUserMail(String name) {
        return userRepository.findByEmail(name);
    }

}
