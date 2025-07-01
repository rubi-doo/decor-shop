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
	// Trang chủ
	@GetMapping("/")
	public String home(Model model) {
		List<Category> parentCategories = categoryService.getAllParentCategoriesWithChildren();
		model.addAttribute("categories", parentCategories);
		
		return "layout/user-layout";
	}

	// Xem sản phẩm theo danh mục
	@GetMapping("/category/{id}")
	public String viewByCategory(@PathVariable Long id, Model model, Pageable pageable) {
	    Category currentCategory = categoryService.findById(id);
	    if (currentCategory == null) return "redirect:/";

	    List<Category> subCategories = categoryService.findChildrenByParentId(id);
	    
	    Page<Product> products;
	    if (!subCategories.isEmpty()) {
	        products = productService.findByCategoryIn(subCategories, pageable); // dùng biến "subCategories" nhưng method chung
	    } else {
	        products = productService.findByCategory(currentCategory, pageable);
	    }

	    Page<Product> page = (products instanceof Page) ? (Page<Product>) products : Page.empty(); // fallback

	    model.addAttribute("parentCategory", currentCategory);
	    model.addAttribute("subCategories", subCategories);
	    model.addAttribute("page", page);
	    model.addAttribute("products", page.getContent());

	    return "user/category-products";
	}



}
