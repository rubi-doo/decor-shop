package com.bkap.controller.admin;

import com.bkap.entity.Banner;
import com.bkap.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
@RequestMapping("/admin/banner")
public class BannerController {

    @Autowired
    private BannerService bannerService;

    // Hiển thị danh sách banner với phân trang
    @GetMapping
    public String listBanners(Model model, @PageableDefault(size = 10) Pageable pageable) {
        Page<Banner> page = bannerService.findAll(pageable);
        model.addAttribute("page", page);
        model.addAttribute("banners", page.getContent());
        return "admin/banner/list";
    }

    // Màn hình tạo mới banner
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("banner", new Banner());
        return "admin/banner/create";
    }

    // Tạo mới banner
    @PostMapping("/create")
    public String createBanner(@ModelAttribute Banner banner,
                              @RequestParam("imageFile") MultipartFile imageFile) throws IOException {
        if (!imageFile.isEmpty()) {
            String fileName = imageFile.getOriginalFilename();
            String uploadDir = System.getProperty("user.dir") + "/uploads/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            banner.setImage("/uploads/" + fileName);
        }
        bannerService.createBanner(banner);
        return "redirect:/admin/banner?success";
    }

    // Màn hình sửa banner
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Banner banner = bannerService.getBannerById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid banner Id:" + id));
        model.addAttribute("banner", banner);
        return "admin/banner/edit";
    }

    // Cập nhật banner
    @PostMapping("/edit/{id}")
    public String updateBanner(@PathVariable("id") Long id,
                              @ModelAttribute Banner banner,
                              @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) throws IOException {
        Banner existingBanner = bannerService.getBannerById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid banner Id:" + id));

        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = imageFile.getOriginalFilename();
            String uploadDir = System.getProperty("user.dir") + "/uploads/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            banner.setImage("/uploads/" + fileName);
        } else {
            banner.setImage(existingBanner.getImage());
        }

        existingBanner.setTitle(banner.getTitle());
        existingBanner.setPriority(banner.getPriority());
        existingBanner.setStatus(banner.getStatus());
        existingBanner.setLink(banner.getLink());
        existingBanner.setPosition(banner.getPosition());
        existingBanner.setImage(banner.getImage());

        bannerService.updateBanner(id, existingBanner);
        return "redirect:/admin/banner?success";
    }

    // Xóa banner
    @GetMapping("/delete/{id}")
    public String deleteBanner(@PathVariable("id") Long id) {
        bannerService.deleteBanner(id);
        return "redirect:/admin/banner";
    }
}