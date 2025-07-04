package com.bkap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bkap.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	List<Category> findByParentIsNull();
	List<Category> findByParentIsNotNull();
	List<Category> findByParentId(Long parentId);
	boolean existsByNameIgnoreCase(String trim);
	
	@Query("""
		    SELECT c FROM Category c
		    WHERE c.parent IS NULL 
		       OR c.parent.id IN (
		           SELECT c2.id FROM Category c2 WHERE c2.parent IS NULL
		       )
		""")
	List<Category> findAllParents();
	

}
