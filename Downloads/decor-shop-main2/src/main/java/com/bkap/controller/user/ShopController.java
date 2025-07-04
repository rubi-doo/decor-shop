package com.bkap.controller.user;

import com.bkap.entity.Category;
import com.bkap.entity.Product;
import com.bkap.service.CategoryService;
import com.bkap.service.ProductService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ShopController {

	@Autowired
	private ProductService productService;

	@Autowired
	private CategoryService categoryService;

	@ModelAttribute("categories")
	public List<Category> getMenuCategories() {
		List<Category> parents = categoryService.getAllParentCategoriesWithChildren();
		for (Category parent : parents) {
			List<Category> children = categoryService.findChildrenByParentId(parent.getId());
			parent.setChildren(children);

			for (Category child : children) {
				List<Category> subChildren = categoryService.findChildrenByParentId(child.getId());
				child.setChildren(subChildren);
			}
		}
		return parents;
	}

	@GetMapping("/category/{id}")
	// Xem sản phẩm theo danh mục
	public String viewByCategory(@PathVariable Long id, @RequestParam(required = false) String keyword,
			@RequestParam(required = false) String sort, @RequestParam(required = false) String inStock,
			Model model, Pageable pageable) {
		
		Category current = categoryService.findById(id);
		if (current == null)
			return "redirect:/";

		Category parentCategory;
		List<Category> subCategories;
		List<Long> categoryIds;

		boolean isLevel2 = current.getParent() != null && current.getParent().getParent() == null;
		boolean isLevel3 = current.getParent() != null && current.getParent().getParent() != null;

		Long filterCategoryId;

		if (isLevel3) {
			parentCategory = current.getParent(); // cấp 2
	        subCategories = categoryService.findChildrenByParentId(parentCategory.getId());
	        categoryIds = List.of(current.getId());
	        
			
		} else if (isLevel2) {
			 parentCategory = current;
			 subCategories = categoryService.findChildrenByParentId(current.getId());

			    if (subCategories.isEmpty()) {
			        categoryIds = List.of(current.getId()); // fallback: dùng chính nó
			    } else {
			        categoryIds = subCategories.stream().map(Category::getId).toList();
			    }
		} else {
			parentCategory = current;
	        subCategories = categoryService.findChildrenByParentId(current.getId());
	        model.addAttribute("parentCategory", parentCategory);
	        model.addAttribute("selectedCategory", current);
	        model.addAttribute("subCategories", subCategories);
	        model.addAttribute("page", Page.empty());
	        model.addAttribute("products", List.of());
	        
	        return "user/category-products";
		}

		Page<Product> products = productService.filterProducts(
		        categoryIds, keyword, sort, inStock, pageable
		    );

		    model.addAttribute("parentCategory", parentCategory);
		    model.addAttribute("selectedCategory", current);
		    model.addAttribute("subCategories", subCategories);
		    model.addAttribute("page", products);
		    model.addAttribute("products", products.getContent());

		    // Giữ lại filter khi render lại view
		    model.addAttribute("keyword", keyword);
		    model.addAttribute("sort", sort);
		    model.addAttribute("inStock", inStock);

		    return "user/category-products";
	}


}
