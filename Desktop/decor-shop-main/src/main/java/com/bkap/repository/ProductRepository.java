package com.bkap.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.bkap.entity.Category;
import com.bkap.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

//	List<Product> findByCategoryId(Long id);
	
	Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

	List<Product> findTop4ByCategoryIdAndIdNot(Long categoryId, Long excludeId);
	List<Product> findTop8ByStatusOrderByIdDesc(int status); // Đã có trong mã mẫu
    List<Product> findTop4ByCategoryIdOrderByIdDesc(Long categoryId); // Thêm phương thức này

	List<Product> findByCategory(Category category);
	
	Page<Product> findByCategory(Category category, Pageable pageable);

	Page<Product> findByCategoryIn(List<Category> categories, Pageable pageable);
	
	List<Product> findTop4ByOrderByCreatedAtDesc();



}
