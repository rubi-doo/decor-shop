package com.bkap.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bkap.entity.User;
import com.bkap.repository.UserRepository;

@Controller
@RequestMapping("/admin")
public class AdminController {
	@Autowired
	private UserRepository userRepository;

	@GetMapping
	public String home() {
		return "admin/dashboard";
	}

	@GetMapping("/users")
	public String listUsers(Model model, @PageableDefault(size = 10) Pageable pageable) {
		Page<User> page = userRepository.findAll(pageable);
		model.addAttribute("page", page);
		model.addAttribute("users", page.getContent());
		return "admin/user/list";
	}

	@GetMapping("/users/role-toggle/{id}")
	public String toggleRole(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		User user = userRepository.findById(id).orElse(null);
		if (user == null) {
			redirectAttributes.addFlashAttribute("error", "Người dùng không tồn tại");
			return "redirect:/admin/users";
		}

		// Không cho admin tự hạ chính mình
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String currentEmail = auth.getName();
		if (user.getEmail().equals(currentEmail)) {
			redirectAttributes.addFlashAttribute("error", "Không thể thay đổi quyền của chính mình!");
			return "redirect:/admin/users";
		}

		// Toggle role
		if ("ADMIN".equalsIgnoreCase(user.getRole())) {
			user.setRole("USER");
			redirectAttributes.addFlashAttribute("success", "Đã hạ quyền người dùng thành công.");
		} else {
			user.setRole("ADMIN");
			redirectAttributes.addFlashAttribute("success", "Đã cấp quyền admin thành công.");
		}

		userRepository.save(user);
		return "redirect:/admin/users";
	}
}
