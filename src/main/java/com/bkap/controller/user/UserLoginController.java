package com.bkap.controller.user;
import com.bkap.service.UserService;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.bkap.dto.RegisterUserDto;
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
        model.addAttribute("registerDto", new RegisterUserDto());
	    return "register";
    }

    @PostMapping("/register")
    public String check_register(@Valid @ModelAttribute("registerDto")  RegisterUserDto userDto,
        BindingResult result,
        Model model) {

        if (result.hasErrors()) {
            return "register"; // Hiển thị lại form với lỗi
        }

        try {
            userService.registerUser(userDto);
            return "redirect:/login?success";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }
    

}
