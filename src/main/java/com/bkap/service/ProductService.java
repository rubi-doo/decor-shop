package com.bkap.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.bkap.entity.Category;
import com.bkap.entity.Product;
import com.bkap.repository.ProductRepository;

@Service
public class ProductService {

	@Autowired
    private ProductRepository productRepository;
	
	 public ProductService(ProductRepository productRepository) {
	        this.productRepository = productRepository;
	    }
	 public List<Product> getAll() {
	        return productRepository.findAll();
	    }

	    public Product save(Product product) {
	        return productRepository.save(product);
	    }
		public Product findById(Long id) {
			return  productRepository.findById(id).orElse(null);
		}
		public void deleteById (Long id) {
			productRepository.deleteById(id);
	    }
		
		public List<Product> findRelatedProducts(Long categoryId, Long excludeProductId) {
			return productRepository.findTop4ByCategoryIdAndIdNot(categoryId, excludeProductId);
		}
		public Page<Product> findAll(Pageable pageable) {
			return productRepository.findAll(pageable);
		}
		
		public List<Product> findByCategory(Category category) {
	        return productRepository.findByCategory(category);
	    }
		
		public Page<Product> findByCategoryId(Long categoryId, Pageable pageable) {
			return productRepository.findByCategoryId(categoryId, pageable);
		}
		 public Page<Product> findByCategory(Category category, Pageable pageable) {
		        return productRepository.findByCategory(category, pageable);
		    }

		    public Page<Product> findByCategoryIn(List<Category> categories, Pageable pageable) {
		        return productRepository.findByCategoryIn(categories, pageable);
		    }

}
