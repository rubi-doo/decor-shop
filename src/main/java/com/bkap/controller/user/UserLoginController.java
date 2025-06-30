package com.bkap.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserLoginController {
    

    @GetMapping("/login")
    public String home() {
	    return "login";
}


}
