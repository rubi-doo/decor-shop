package com.bkap.specification;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.bkap.entity.Product;

public class ProductSpecifications {
	public static Specification<Product> hasKeyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.trim().isEmpty()) return null;
            return cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%");
        };
    }

    public static Specification<Product> isInStock(String inStock) {
        return (root, query, cb) -> {
            if (!"true".equalsIgnoreCase(inStock)) return null;
            return cb.greaterThan(root.get("quantity"), 0);
        };
    }

    public static Specification<Product> belongsToCategoryIn(List<Long> categoryIds) {
        return (root, query, cb) -> {
        	if (categoryIds == null || categoryIds.isEmpty()) return null;
            return root.get("category").get("id").in(categoryIds);
        };
    }

}
