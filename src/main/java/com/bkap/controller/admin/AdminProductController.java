package com.bkap.controller.admin;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bkap.entity.Product;
import com.bkap.service.CategoryService;
import com.bkap.service.ProductService;
import com.bkap.util.ThymeleafUtils;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
public class AdminProductController {

	@Autowired
	private ProductService productService;
	@Autowired
	private CategoryService categoryService;
	
	@ModelAttribute("utils")
    public ThymeleafUtils utils() {
        return new ThymeleafUtils();
    }

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
	    
	    int currentPage = page.getNumber(); // bắt đầu từ 0
	    int pageSize = page.getSize();
	    int startItem = currentPage * pageSize + 1;
	    int endItem = Math.min(startItem + page.getNumberOfElements() - 1, (int) page.getTotalElements());

	    model.addAttribute("startItem", startItem);
	    model.addAttribute("endItem", endItem);
	    model.addAttribute("totalItems", page.getTotalElements());
	    model.addAttribute("page", page);
	    model.addAttribute("products", page.getContent());
	    

	    // Giữ lại keyword và sort trên view
	    model.addAttribute("keyword", keyword);
	    model.addAttribute("sort", sort);

	    return "admin/product/list";
	}

	@GetMapping("/products/view/{id}")
	public String view(@PathVariable Long id, Model model) {
	    Product product = productService.findById(id);
	    if (product == null) {
	        return "redirect:/admin/products";
	    }
	    model.addAttribute("product", product);
	    return "admin/product/view"; // view.html
	}

	@GetMapping("/products/create")
	public String showCreateForm(Model model) {
	    Product product = new Product();
	    product.setStatus(1); // Mặc định là "Hiển thị"
	    model.addAttribute("product", product);
	    model.addAttribute("categories", categoryService.findAll());
	    return "admin/product/create";
	}

	@PostMapping("/products/create")
	public String save( @Valid @ModelAttribute("product") Product product,
	                   BindingResult result,
	                   @RequestParam("imageFile") MultipartFile imageFile,
	                   Model model,
	                   RedirectAttributes redirectAttributes) throws IOException {

	    // Validate: Mã sản phẩm đã tồn tại
	    if (productService.existsByProductCode(product.getProductCode())) {
	        result.rejectValue("productCode", "error.product", "Mã sản phẩm đã tồn tại");
	    }
	    
	    // validate ảnh bắt buộc và phải nhỏ hơn hoặc bằng 10MB
	    if (imageFile == null || imageFile.isEmpty()) {
	        result.rejectValue("imageUrl", "error.product", "Ảnh là bắt buộc");
	    } else if (imageFile.getSize() > 10 * 1024 * 1024) {
	        result.rejectValue("imageUrl", "error.product", "Ảnh không được vượt quá 10MB");
	    }

	    // Validate: Giá khuyến mãi <= giá gốc
	    if (product.getSalePrice() != null && product.getPrice() != null &&
	        product.getSalePrice() > product.getPrice()) {
	        result.rejectValue("salePrice", "error.product", "Giá khuyến mãi không được lớn hơn giá gốc");
	    }

	    try {
	        if (!imageFile.isEmpty()) {
	            String originalFileName = Paths.get(imageFile.getOriginalFilename()).getFileName().toString();
	            Path uploadDir = Paths.get("uploads");
	            Files.createDirectories(uploadDir);

	            Path filePath = uploadDir.resolve(originalFileName);
	            Files.write(filePath, imageFile.getBytes());

	            product.setImageUrl(originalFileName); // lưu đúng tên ảnh gốc vào DB
	        }
	    } catch (IOException e) {
	        result.rejectValue("imageUrl", "error.product", "Không thể lưu ảnh: " + e.getMessage());
	        model.addAttribute("categories", categoryService.findAllChildCategories());
	        return "admin/product/create";
	    }

	    
	    // Nếu có lỗi → quay lại form
	    if (result.hasErrors()) {
	        model.addAttribute("categories", categoryService.findAllChildCategories());
	        return "admin/product/create";
	    }

	    // Set thời gian
	    LocalDateTime now = LocalDateTime.now();
	    product.setCreatedAt(now);
	    product.setUpdatedAt(now);

	    productService.save(product);
	    redirectAttributes.addFlashAttribute("success", "Thêm sản phẩm thành công!");
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
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("product") Product model,
                         BindingResult result,
                         @RequestParam("imageFile") MultipartFile imageFile,
                         Model viewModel,
                         RedirectAttributes redirectAttributes) throws IOException {

        Product existing = productService.findById(model.getId());
        if (existing == null) {
            return "redirect:/admin/products";
        }

        // Check trùng mã sản phẩm (nếu đổi mã mới và mã đã tồn tại ở bản ghi khác)
        if (!existing.getProductCode().equals(model.getProductCode())
                && productService.existsByProductCode(model.getProductCode())) {
            result.rejectValue("productCode", "error.product", "Mã sản phẩm đã tồn tại");
        }

        // Validate ảnh ≤ 10MB
        if (!imageFile.isEmpty()) {
            if (imageFile.getSize() > 10 * 1024 * 1024) {
                result.rejectValue("imageUrl", "error.product", "Ảnh không được vượt quá 10MB");
            }
        }

        // Validate salePrice ≤ price
        if (model.getSalePrice() != null && model.getPrice() != null &&
            model.getSalePrice() > model.getPrice()) {
            result.rejectValue("salePrice", "error.product", "Giá khuyến mãi không được lớn hơn giá gốc");
        }

        if (result.hasErrors()) {
            viewModel.addAttribute("categories", categoryService.findAll());
            return "admin/product/edit";
        }

        // Cập nhật thông tin
        existing.setName(model.getName());
        existing.setProductCode(model.getProductCode());
        existing.setPrice(model.getPrice());
        existing.setSalePrice(model.getSalePrice());
        existing.setDescription(model.getDescription());
        existing.setQuantity(model.getQuantity());
        existing.setStatus(model.getStatus());
        existing.setCategory(model.getCategory());
        existing.setBrand(model.getBrand());
        existing.setSize(model.getSize());
        existing.setColor(model.getColor());
        existing.setMaterial(model.getMaterial());

        if (!imageFile.isEmpty()) {
            String fileName = Paths.get(imageFile.getOriginalFilename()).getFileName().toString();

            Path uploadPath = Paths.get("uploads");
            Files.createDirectories(uploadPath);

            Path filePath = uploadPath.resolve(fileName);
            imageFile.transferTo(filePath.toFile());

            existing.setImageUrl(fileName);
        }

        productService.save(existing);
        redirectAttributes.addFlashAttribute("success", "Cập nhật sản phẩm thành công!");
        return "redirect:/admin/products";
    }

	
	@GetMapping("/products/delete/{id}")
	
	public String delete(@PathVariable("id") Long id) {
		productService.deleteById(id);
		return "redirect:/admin/products";
	}
}
