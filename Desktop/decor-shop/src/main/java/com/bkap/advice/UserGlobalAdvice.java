package com.bkap.advice;

import com.bkap.service.CategoryService;
import com.bkap.entity.Category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice(basePackages = "com.bkap.controller.user") // Áp dụng cho các controller user
public class UserGlobalAdvice {

    @Autowired
    private CategoryService categoryService;

    @ModelAttribute("categories")
    public List<Category> categoriesForMenu() {
        return categoryService.getAllParentCategoriesWithChildren();
    }
}

