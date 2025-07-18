package com.bkap.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bkap.entity.User;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    
}

