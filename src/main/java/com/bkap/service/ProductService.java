package com.bkap.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.bkap.entity.Category;
import com.bkap.entity.Product;
import com.bkap.repository.ProductRepository;
import com.bkap.specification.ProductSpecifications;

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
    	Product saved = productRepository.save(product);
        System.out.println("Đã lưu sản phẩm: " + saved);
        return saved;
    }

    public Product findById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public void deleteById(Long id) {
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

    public Page<Product> filterProducts(List<Long> categoryIds, String keyword, String sort, String inStock,
                                        Pageable pageable) {

        Specification<Product> spec = ProductSpecifications.belongsToCategoryIn(categoryIds)
                .and(ProductSpecifications.hasKeyword(keyword))
                .and(ProductSpecifications.isInStock(inStock));

        Sort sortBy;
        switch (sort != null ? sort : "") {
            case "priceAsc" -> sortBy = Sort.by("price").ascending();
            case "priceDesc" -> sortBy = Sort.by("price").descending();
            case "nameAsc" -> sortBy = Sort.by("name").ascending();
            case "nameDesc" -> sortBy = Sort.by("name").descending();
            default -> sortBy = Sort.by("createdAt").descending(); // default
        }

        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortBy);
        return productRepository.findAll(spec, sortedPageable);
    }

    public List<Product> getLatestProducts() {
        return productRepository.findTop4ByOrderByCreatedAtDesc();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

	public boolean existsByProductCode(String productCode) {
		return productRepository.existsByProductCode(productCode);
	}
}
