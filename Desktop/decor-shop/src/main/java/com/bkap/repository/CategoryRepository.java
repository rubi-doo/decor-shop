package com.bkap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bkap.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	List<Category> findByParentIsNull();
	List<Category> findByParentIsNotNull();
	List<Category> findByParentId(Long parentId);
	

}
