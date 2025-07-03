package com.bkap.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.Authentication;

import com.bkap.entity.User;
import com.bkap.repository.UserRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping({ "", "/" })
    public String main(Model model, Authentication auth) {
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String email = auth.getName();
            User user = userRepository.findByEmail(email).orElseThrow();
            model.addAttribute("user", user);
        }
        return "user/profile/main"; // HTML này sẽ dùng layout:decorate
    }

    @GetMapping("/update")
    public String update(Model model, Authentication auth) {
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String email = auth.getName();
            User user = userRepository.findByEmail(email).orElseThrow();
            model.addAttribute("user", user);
        }
        return "user/profile/update";
    }

@PostMapping("")
public String updateProfile(@Validated(OnUpdate.class) @ModelAttribute("user") User updatedUser,
                            BindingResult bindingResult,
                            Authentication auth,
                            RedirectAttributes redirectAttributes,
                            Model model) {
    if (bindingResult.hasErrors()) {
        model.addAttribute("user", updatedUser);
        return "user/profile/update";
    }

    String email = auth.getName();
    User user = userRepository.findByEmail(email).orElseThrow();

    user.setName(updatedUser.getName());
    user.setPhone(updatedUser.getPhone());
    user.setAddress(updatedUser.getAddress());

    userRepository.save(user);

    redirectAttributes.addFlashAttribute("success", "Cập nhật thành công!");
    return "redirect:/profile";
}

}
