package com.bkap.controller.user;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.bkap.entity.Banner;
import com.bkap.entity.Product;
import com.bkap.repository.ProductRepository;
import com.bkap.service.BannerService;
import com.bkap.service.ProductService;

@Controller
public class HomeController {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private BannerService bannerService;
    @Autowired
    private ProductService productService;

    @GetMapping("/")
    public String home(Model model) {
        try {
            List<Product> latestProducts = productRepository.findTop8ByStatusOrderByIdDesc(1);
            if (latestProducts.size() > 8) {
                latestProducts = latestProducts.subList(0, 8);
            }
            latestProducts.removeIf(product -> product == null);
            List<Banner> banners = bannerService.getAllBanners();
            System.out.println("Latest Products: " + latestProducts);
            System.out.println("Banners: " + banners);
            model.addAttribute("latestProducts", latestProducts);
            model.addAttribute("banners", banners);
            model.addAttribute("showBanner", true);
        } catch (Exception e) {
            System.err.println("Error in HomeController: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Không thể tải dữ liệu trang chủ: " + e.getMessage());
            return "error";
        }
        return "home";
    }

    @GetMapping("/product-detail/{id}")
    public String productDetail(Model model, @PathVariable Long id) {
        try {
            Optional<Product> optionalProduct = productRepository.findById(id);
            if (optionalProduct.isPresent()) {
                Product product = optionalProduct.get();
                System.out.println("Product found: " + product);
                model.addAttribute("product", product);

                Long categoryId = product.getCategory() != null ? product.getCategory().getId() : null;
                List<Product> relatedProducts = (categoryId != null) 
                    ? productService.findRelatedProducts(categoryId, product.getId()) 
                    : List.of();
                System.out.println("Related Products: " + relatedProducts);
                model.addAttribute("products", relatedProducts);
            } else {
                model.addAttribute("error", "Sản phẩm không tồn tại với ID: " + id);
                return "error";
            }

            List<Banner> banners = bannerService.getAllBanners();
            System.out.println("Banners: " + banners);
            model.addAttribute("banners", banners);
            model.addAttribute("showBanner", false);
        } catch (Exception e) {
            System.err.println("Error in HomeController (productDetail): " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Không thể tải chi tiết sản phẩm: " + e.getMessage());
            return "error";
        }
        return "product-detail";
    }

    @GetMapping("/wishlist")
    public String wishlist(Model model, @RequestParam(value = "addProductId", required = false) Long addProductId) {
        try {
            // Lấy danh sách sản phẩm trong wishlist (giả sử có service hoặc repository xử lý)
            // Đây là ví dụ, bạn cần triển khai logic thực tế với database
            List<Product> wishlistProducts = productService.getWishlistProducts(); // Cần triển khai phương thức này
            model.addAttribute("products", wishlistProducts);

            // Xử lý thêm sản phẩm vào wishlist nếu có tham số addProductId
            if (addProductId != null) {
                Optional<Product> optionalProduct = productRepository.findById(addProductId);
                if (optionalProduct.isPresent()) {
                    Product product = optionalProduct.get();
                    // Gọi service để thêm vào wishlist (cần triển khai)
                    productService.addToWishlist(addProductId);
                    System.out.println("Added to wishlist: " + product.getName());
                }
            }
        } catch (Exception e) {
            System.err.println("Error in HomeController (wishlist): " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Không thể tải danh sách yêu thích: " + e.getMessage());
            return "error";
        }
        return "wishlist";
    }
}