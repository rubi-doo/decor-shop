package com.bkap.controller.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.bkap.entity.Product;
import com.bkap.entity.User;
import com.bkap.repository.UserRepository;
import com.bkap.service.ProductService;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class HomeController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String home(Model model) {
        List<Product> latestProducts = productService.getLatestProducts();
        model.addAttribute("latestProducts", latestProducts);
        return "home";
    }

 
    
}

