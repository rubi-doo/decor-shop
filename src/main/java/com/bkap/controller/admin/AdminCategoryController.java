package com.bkap.controller.admin;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bkap.entity.Category;
import com.bkap.service.CategoryService;

import jakarta.validation.Valid;

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
    	model.addAttribute("parentCategories", categoryService.findAllParents());
    	category.setStatus(1);
    	
    	return "admin/category/create";
    }
	
	@PostMapping("/categories/create")
	public String save(@Valid @ModelAttribute Category category,
	                   BindingResult result,
	                   Model model,
	                   RedirectAttributes redirectAttributes) {

	    // Check trùng tên
	    if (categoryService.existsByName(category.getName())) {
	        result.rejectValue("name", "error.category", "Tên danh mục đã tồn tại");
	    }

	    // Check lỗi form
	    if (result.hasErrors()) {
	        model.addAttribute("parentCategories", categoryService.findAllParents());
	        return "admin/category/create";
	    }

	    // Lưu danh mục
	    categoryService.save(category);

	    // Gửi message về view sau khi redirect
	    redirectAttributes.addFlashAttribute("success", "Thêm danh mục thành công!");

	    return "redirect:/admin/categories";
	}

    
	@GetMapping("/categories/edit/{id}")
	public String edit(@PathVariable Long id, Model model,
	                   @RequestParam(name = "success", required = false) String successMsg) {
	    Category category = categoryService.findById(id);
	    if (category == null) {
	        return "redirect:/admin/categories";
	    }

	    model.addAttribute("category", category);

	    // Lấy cấp 1 + cấp 2 trừ chính nó
	    List<Category> parentCategories = categoryService.findAllParents()
	            .stream()
	            .filter(c -> !c.getId().equals(id))
	            .toList();

	    model.addAttribute("parentCategories", parentCategories);
	    if (successMsg != null) {
	        model.addAttribute("success", successMsg);
	    }

	    return "admin/category/edit";
	}


    
	@PostMapping("/categories/edit/{id}")
	public String update(@PathVariable Long id,
	                     @Valid @ModelAttribute("category") Category category,
	                     BindingResult result,
	                     Model model,
	                     RedirectAttributes redirectAttributes) {

	    // Tránh gán chính nó làm cha
	    if (category.getParent() != null && category.getParent().getId().equals(category.getId())) {
	        result.rejectValue("parent", "error.category", "Danh mục không thể là cha của chính nó");
	    }

	    // Check tên bị trùng (nếu thay đổi tên)
	    Category existing = categoryService.findById(id);
	    if (!existing.getName().equals(category.getName())
	            && categoryService.existsByName(category.getName())) {
	        result.rejectValue("name", "error.category", "Tên danh mục đã tồn tại");
	    }

	    if (result.hasErrors()) {
	        List<Category> parentCategories = categoryService.findAllParents()
	                .stream()
	                .filter(c -> !c.getId().equals(id))
	                .toList();
	        model.addAttribute("parentCategories", parentCategories);
	        return "admin/category/edit";
	    }

	    category.setId(id); // để chắc chắn ID không bị mất
	    categoryService.save(category);
	    redirectAttributes.addFlashAttribute("success", "Cập nhật danh mục thành công");
	    return "redirect:/admin/categories";
	}

    
	@GetMapping("/categories/delete/{id}")
	public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
	    Category category = categoryService.findById(id);

	    if (category == null) {
	        redirectAttributes.addFlashAttribute("error", "Danh mục không tồn tại.");
	        return "redirect:/admin/categories";
	    }

	    // Kiểm tra có sản phẩm không
	    if (category.getProducts() != null && !category.getProducts().isEmpty()) {
	        redirectAttributes.addFlashAttribute("error", "Không thể xóa danh mục thành công vì còn sản phẩm thuộc danh mục này.");
	        return "redirect:/admin/categories";
	    }

	    // Kiểm tra có danh mục con không
	    if (category.getChildren() != null && !category.getChildren().isEmpty()) {
	        redirectAttributes.addFlashAttribute("error", "Không thể xóa danh mục thành công vì còn danh mục con.");
	        return "redirect:/admin/categories";
	    }

	    categoryService.deleteById(id);
	    redirectAttributes.addFlashAttribute("success", "Xóa danh mục thành công.");
	    return "redirect:/admin/categories";
	}
	
	@GetMapping("/categories/{id}/products")
	public String viewProductsByCategory(@PathVariable Long id, Model model) {
	    Category category = categoryService.findById(id);
	    if (category == null) {
	        return "redirect:/admin/categories";
	    }
	    model.addAttribute("category", category);
	    model.addAttribute("products", category.getProducts());
	    return "admin/category/view-by-category";
	}


}
