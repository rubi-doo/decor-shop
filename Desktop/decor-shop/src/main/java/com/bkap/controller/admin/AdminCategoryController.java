package com.bkap.controller.admin;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bkap.entity.Category;
import com.bkap.service.CategoryService;

@Controller
@RequestMapping("/admin")
public class AdminCategoryController {
	@Autowired
    private CategoryService categoryService;
	
	@GetMapping("/categories")
    public String view(Model model, @PageableDefault(size = 10) Pageable pageable ) {
    	 Page<Category> page = categoryService.findAll(pageable);
    	    model.addAttribute("page", page);
    	    model.addAttribute("categories", page.getContent());
        return "admin/category/list";
    }
	
	@GetMapping("/categories/create")
    public String create(Model model) {
    	Category category = new Category();
    	model.addAttribute("category", category);
    	model.addAttribute("parentCategories", categoryService.getAllParentCategoriesWithChildren());
    	category.setStatus(1);
    	
    	return "admin/category/create";
    }
    @PostMapping("/categories/create")
    public String save(@ModelAttribute Category category) {
    	 categoryService.save(category);
         return "redirect:/admin/categories";
    }
    
    @GetMapping("/categories/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Category category = categoryService.findById(id);
        if (category == null) {
            return "redirect:/admin/categories";
        }
        model.addAttribute("category", category);

        // Lấy danh sách các danh mục cha (trừ chính nó)
        List<Category> parentCategories = categoryService.getAllParentCategoriesWithChildren()
                                                         .stream()
                                                         .filter(c -> !c.getId().equals(id)) // tránh cho phép chọn chính nó làm cha
                                                         .toList();
        model.addAttribute("parentCategories", parentCategories);
        
        return "admin/category/edit";
    }

    
    @PostMapping("/categories/edit/{id}")
    public String update(@ModelAttribute Category category) {
    	categoryService.save(category);
        return "redirect:/admin/categories";
    }
    
    @GetMapping("/categories/delete/{id}")
    public String delete(@PathVariable Long id) {
    	categoryService.deleteById(id);
    	return "redirect:/admin/categories";
    }

}
