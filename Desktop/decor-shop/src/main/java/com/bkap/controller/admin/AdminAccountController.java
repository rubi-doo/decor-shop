package com.bkap.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class AdminAccountController {
    
    @GetMapping("/admin/login")
    public String Login() {
        return "admin/login";
    }
    @GetMapping("/403")
    public String accessDenied() {
        return "error/403"; // dẫn đến file templates/error/403.html
    }
}
