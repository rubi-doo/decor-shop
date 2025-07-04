package com.bkap.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bkap.entity.Banner;

public interface BannerRepository extends JpaRepository<Banner, Long> {
}
