package com.bkap.controller.user;
import com.bkap.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.bkap.entity.User;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class UserLoginController {

    private final UserService userService;


    UserLoginController(UserService userService) {
        this.userService = userService;
    }
    

    @GetMapping("/login")
    public String Login() {
	    return "login";
    }

    @GetMapping("/register")
    public String Register(Model model) {
        model.addAttribute("user", new User());
	    return "register";
    }

    @PostMapping("/register")
    public String check_register(@ModelAttribute User user , Model model) {
        try {
            userService.registerUser(user);
            return "redirect:/login?success";
        } catch (Exception e) {
            // TODO: handle exception
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }
    

}
