package com.bkap.controller.user;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bkap.cart.CartItem;
import com.bkap.dto.CartItemDTO;
import com.bkap.entity.Order;
import com.bkap.entity.Product;
import com.bkap.entity.User;
import com.bkap.service.CartService;
import com.bkap.service.OrderService;
import com.bkap.service.ProductService;
import com.bkap.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class PaymentController {
  
	 @Autowired
	    private CartService cartService;

	    @Autowired
	    private ProductService productService;
	    
	    @Autowired
	    private UserService  userService;
	    
	    
	    @Autowired
	    private OrderService  orderService;

	    @GetMapping("/checkout")
	    public String showCheckoutPage(HttpSession session, Model model, Principal principal,  RedirectAttributes redirectAttributes) {
	        // Nếu chưa đăng nhập (principal là null) thì redirect sang trang đăng nhập
	        if (principal == null) {
	        	redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập để thanh toán.");
	            return "redirect:/login"; // hoặc thay bằng trang bạn muốn
	        }

	        List<CartItem> cartItems = cartService.getCart(session);

	        List<CartItemDTO> cartItemDTOs = cartItems.stream().map(item -> {
	            CartItemDTO dto = new CartItemDTO();
	            dto.setProductId(item.getProductId());
	            dto.setQuantity(item.getQuantity());
	            return dto;
	        }).collect(Collectors.toList());

	        List<CartItemDisplay> cartItemDisplays = cartItems.stream().map(item -> {
	            Product product = productService.getProductById(item.getProductId()); 
	            return new CartItemDisplay(
	                    product.getId(),
	                    product.getName(),
	                    item.getQuantity(),
	                    product.getPrice()
	            );
	        }).collect(Collectors.toList());

	        model.addAttribute("cartItems", cartItemDisplays);
	        model.addAttribute("cartItemsJson", cartItemDTOs); 
	        model.addAttribute("total", cartService.getTotal(session));
	        return "checkout";
	    }

	    @GetMapping("/payment-result")
	    public String showPaymentResult(@RequestParam("status") String status, Model model, HttpSession session) {
	        if ("00".equals(status)) {
	            cartService.clearCart(session); 
	        }
	        model.addAttribute("status", status);
	        return "payment-result";
	    }
	   
	    @GetMapping("/order-confirmed")
	    public String orderConfirmedPage() {
	        return "order-confirmed"; 
	    }

	    
	}

    

	// Class tạm thời để hiển thị thông tin giỏ hàng trong view
	class CartItemDisplay {
	    private Long productId;
	    private String productName;
	    private int quantity;
	    private double price;

	    public CartItemDisplay(Long productId, String productName, int quantity, double price) {
	        this.productId = productId;
	        this.productName = productName;
	        this.quantity = quantity;
	        this.price = price;
	    }

	    // Getters
	    public Long getProductId() { return productId; }
	    public String getProductName() { return productName; }
	    public int getQuantity() { return quantity; }
	    public double getPrice() { return price; }
	}
