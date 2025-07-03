package com.bkap.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bkap.entity.Order;
import com.bkap.entity.User;

public interface OrderRepository extends JpaRepository<Order, Long> {
	List<Order> findByUserId(Long userId);
	Page<Order> findByUserIdOrderByOrderDateDesc(Long userId, Pageable pageable);
    Optional<Order> findByTransactionId(String transactionId);
    List<Order> findByPaymentStatus(String paymentStatus);
    
   
}

