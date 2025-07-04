package com.bkap.controller.user;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import com.bkap.dto.ChangePasswordForm;
import com.bkap.dto.UpdateUserDto;
import com.bkap.entity.Order;
import com.bkap.entity.User;
import com.bkap.repository.UserRepository;
import com.bkap.service.OrderService;
import com.bkap.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping({ "", "/" })
    public String main(Model model, Authentication auth) {

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String email = auth.getName();
            User user = userRepository.findByEmail(email).orElseThrow();
            model.addAttribute("user", user);
            return "user/profile/main";
        } else {
            // Nếu người dùng chưa đăng nhập, chuyển hướng đến trang đăng nhập
            model.addAttribute("error", "Vui lòng đăng nhập để truy cập trang cá nhân.");
            return "redirect:/login";
        } // Hoặc bạn có thể trả về một trang thông báo lỗi
          // HTML này sẽ dùng layout:decorate
    }

    @GetMapping("/update")
    public String update(Model model, Authentication auth) {
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String email = auth.getName();
            User user = userRepository.findByEmail(email).orElseThrow();

            UpdateUserDto dto = new UpdateUserDto();
            dto.setId(user.getId());
            dto.setName(user.getName());
            dto.setPhone(user.getPhone());
            dto.setAddress(user.getAddress());
            dto.setEmail(user.getEmail());

            model.addAttribute("updateUser", dto);
        }
        return "user/profile/update";
    }

    @PostMapping("")
    public String updateProfile(@Valid @ModelAttribute("updateUser") UpdateUserDto updatedUserDto,
            BindingResult bindingResult, Authentication auth, RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            return "user/profile/update";
        }
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElseThrow();

        user.setName(updatedUserDto.getName());
        user.setPhone(updatedUserDto.getPhone());
        user.setAddress(updatedUserDto.getAddress());

        userRepository.save(user);

        redirectAttributes.addFlashAttribute("success", "Cập nhật thành công!");
        return "redirect:/profile";
    }

    @GetMapping("/my-orders")
    public String MyOrders(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Principal principal, Model model) {
        if (principal == null) {
            model.addAttribute("error", "Vui lòng đăng nhập để xem đơn hàng.");
            return "user/profile/my-orders";
        }

        String email = principal.getName();
        User user = userService.findByUserMail(email).orElse(null);
        if (user == null) {
            model.addAttribute("error", "Người dùng không tồn tại.");
            return "user/profile/my-orders";
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
        Page<Order> ordersPage = orderService.getOrdersByUserId(user.getId(), pageable);

        Map<Long, BigDecimal> tongTienMoiDon = new HashMap<>();
        for (Order don : ordersPage.getContent()) {
            BigDecimal tongTien = orderService.calculateTotalAmount(don);
            tongTienMoiDon.put(don.getId(), tongTien);
        }

        model.addAttribute("ordersPage", ordersPage);
        model.addAttribute("orders", ordersPage.getContent());
        model.addAttribute("orderTotals", tongTienMoiDon);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", ordersPage.getTotalPages());

        return "user/profile/my-orders";
    }

    @GetMapping("/my-orders/{orderId}")
    public String showOrderDetails(@PathVariable Long orderId, Principal principal, Model model) {
        String email = principal.getName();
        User user = userService.findByUserMail(email).orElse(null);

        if (user == null) {
            model.addAttribute("error", "Vui lòng đăng nhập để xem đơn hàng.");
            return "user/profile/order-details";
        }

        try {
            Order order = orderService.getOrderByIdAndUserId(orderId, user.getId());
            model.addAttribute("order", order);

            BigDecimal totalAmount = order.getOrderDetails().stream()
                    .map(detail -> BigDecimal.valueOf(detail.getPrice())
                            .multiply(BigDecimal.valueOf(detail.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            model.addAttribute("totalAmount", totalAmount);

            return "user/profile/order-details";
        } catch (IllegalAccessException e) {
            model.addAttribute("error", "Bạn không có quyền xem đơn hàng này.");
            return "user/profile/order-details";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Đơn hàng không tồn tại.");
            return "user/profile/order-details";
        }
    }

    @GetMapping("/change-password")
    public String changePasswordPage(Model model) {
        model.addAttribute("changePasswordForm", new ChangePasswordForm());
        return "user/profile/change-password"; // hoặc đường dẫn tương ứng
    }

    @PostMapping("/change-password")
public String handleChangePassword(@Valid @ModelAttribute("changePasswordForm") ChangePasswordForm form,
                                   BindingResult result,
                                   Authentication auth,
                                   HttpServletRequest request,
                                   HttpServletResponse response,
                                   RedirectAttributes redirect) {

    if (result.hasErrors()) {
        return "user/profile/change-password";
    }

    String email = auth.getName();
    User user = userRepository.findByEmail(email).orElseThrow();

    // So sánh mật khẩu cũ
    if (!passwordEncoder.matches(form.getOldPassword(), user.getPassword())) {
        result.rejectValue("oldPassword", "error.oldPassword", "Mật khẩu cũ không đúng");
        return "user/profile/change-password";
    }

    if (!form.getNewPassword().equals(form.getConfirmPassword())) {
        result.rejectValue("confirmPassword", "error.confirmPassword", "Xác nhận mật khẩu không khớp");
        return "user/profile/change-password";
    }

    // Cập nhật mật khẩu mới
    user.setPassword(passwordEncoder.encode(form.getNewPassword()));
    userRepository.save(user);

    // Logout người dùng
    new SecurityContextLogoutHandler().logout(request, response, auth);

    // Gửi thông báo nếu cần
    redirect.addFlashAttribute("successMessage", "Đổi mật khẩu thành công. Vui lòng đăng nhập lại.");

    // Chuyển về trang login
    return "redirect:/login";
}
}
