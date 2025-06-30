package com.bkap.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    
    @Autowired
    private UserRepository userRepository;

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
}
