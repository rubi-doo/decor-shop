package com.bkap.controller.admin;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.bkap.entity.Category;
import com.bkap.entity.Product;
import com.bkap.repository.ProductRepository;
import com.bkap.service.CategoryService;
import com.bkap.service.ProductService;

@Controller
@RequestMapping("/admin")
public class AdminProductController {

	@Autowired
	private ProductService productService;
	@Autowired
	private CategoryService categoryService;

	
	@Autowired
	private ProductRepository productRepository;

	@GetMapping("/products")
	public String view(
	        @RequestParam(name = "keyword", required = false) String keyword,
	        @RequestParam(name = "sort", required = false) String sort,
	        @PageableDefault(size = 10) Pageable pageable,
	        Model model) {

	
	    Page<Product> page = productService.filterProducts(
	            null,       // categoryIds chưa cần lọc danh mục
	            keyword,
	            sort,
	            null,       // inStock chưa dùng
	            pageable
	    );

	    model.addAttribute("page", page);
	    model.addAttribute("products", page.getContent());

	    // Giữ lại keyword và sort trên view
	    model.addAttribute("keyword", keyword);
	    model.addAttribute("sort", sort);

	    return "admin/product/list";
	}


	@GetMapping("/products/create")
	public String create(Model model) {
		Product product = new Product();
		model.addAttribute("product", product);
		product.setStatus(1);
		model.addAttribute("categories", categoryService.findAllChildCategories());
		return "admin/product/create";
	}

	@PostMapping("/products/create")
	public String save(@ModelAttribute("product") Product product,
			@RequestParam("imageFile") MultipartFile imageFile) throws IOException {
		if (!imageFile.isEmpty()) {
			String fileName = imageFile.getOriginalFilename();
			Path path = Paths.get("src/main/resources/static/images/" + fileName);
			Files.write(path, imageFile.getBytes());
			product.setImageUrl(fileName);
		}

		productService.save(product);
		return "redirect:/admin/products";
	}
	
	@GetMapping("/products/edit/{id}")
	public String edit(@PathVariable Long id, Model model) {
		 Product product = productService.findById(id);
		 if (product == null) {
			 return "redirect:/admin/products";
		 }
		 model.addAttribute("product", product);
		 model.addAttribute("categories", categoryService.findAll()); 
		 return "admin/product/edit";
	}
	@PostMapping("/products/edit/{id}")
	public String update(@RequestParam("imageFile") MultipartFile file,
	                     @ModelAttribute Product model) {
	    Optional<Product> optional = productRepository.findById(model.getId());
	    if (optional.isEmpty()) {
	        return "redirect:/admin/product";
	    }

	    Product existing = optional.get();

	    // Cập nhật field
	    existing.setName(model.getName());
	    existing.setPrice(model.getPrice());
	    existing.setDescription(model.getDescription());
	    existing.setCategory(model.getCategory());
	    existing.setStatus(model.getStatus());

	    // Nếu có ảnh mới
	    if (file != null && !file.isEmpty()) {
	        try {
	            String uploadDir = System.getProperty("user.dir") + "/uploads";
	            Path uploadPath = Paths.get(uploadDir);
	            if (!Files.exists(uploadPath)) {
	                Files.createDirectories(uploadPath);
	            }

	            String filename = file.getOriginalFilename();
	            Path filePath = uploadPath.resolve(filename);
	            file.transferTo(filePath.toFile());

	            existing.setImageUrl(filename);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

	    productRepository.save(existing); // Chỉ lưu bản gốc
	    return "redirect:/admin/products";
	}

	
	@GetMapping("/products/delete/{id}")
	
	public String delete(@PathVariable("id") Long id) {
		productService.deleteById(id);
		return "redirect:/admin/products";
	}
}
