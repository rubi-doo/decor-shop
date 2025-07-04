package com.bkap.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.bkap.entity.Category;
import com.bkap.entity.Product;
import com.bkap.repository.CategoryRepository;
import com.bkap.repository.ProductRepository;

@Service
public class CategoryService {
	
	private final CategoryRepository categoryRepository;
	private final ProductRepository productRepository;
	
	@Autowired
    public CategoryService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
		this.productRepository = null;
    }

    public Page<Category> findAll(Pageable pageable) {
		return categoryRepository.findAll(pageable);
	}

	public List<Category> getAllParentCategoriesWithChildren() {
		 return categoryRepository.findByParentIsNull();
	}
	
	 public Category save(Category category) {
		 if (category.getId() == null) {
		        category.setCreatedAt(LocalDateTime.now());
		    }
		    category.setUpdatedAt(LocalDateTime.now());
	        return categoryRepository.save(category);
	        
	    }

	public Category findById(Long id) {
		return categoryRepository.findById(id).orElse(null);
	}

	public void deleteById(Long id) {
		categoryRepository.deleteById(id);
		
	}

	public List<Category> findAllChildCategories() {
		return categoryRepository.findByParentIsNotNull();
	}

	public List<Category> findAll() {
		return categoryRepository.findAll();
	}

	public List<Category> findAllWithoutPagination() {
		return categoryRepository.findAll();
				
	}

	public List<Category> findChildrenByParentId(Long parentId) {
		return categoryRepository.findByParentId(parentId);
	}

	public boolean existsByName(String name) {
	    return categoryRepository.existsByNameIgnoreCase(name.trim());
	}

	public List<Category> findAllParents() {
	    return categoryRepository.findAllParents();
	}


	
}
	
