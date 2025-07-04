package com.bkap.service;

import com.bkap.entity.Banner;
import com.bkap.repository.BannerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class BannerService {

    @Autowired
    private BannerRepository bannerRepository;

    // Lấy danh sách tất cả banner
    public List<Banner> getAllBanners() {
        return bannerRepository.findAll();
    }

    // Tạo mới banner
    public Banner createBanner(Banner banner) {
        return bannerRepository.save(banner);
    }

    // Lấy banner theo ID
    public Optional<Banner> getBannerById(Long id) {
        return bannerRepository.findById(id);
    }

    // Sửa banner
    public Banner updateBanner(Long id, Banner bannerDetails) {
        Optional<Banner> banner = bannerRepository.findById(id);
        if (banner.isPresent()) {
            Banner updatedBanner = banner.get();
            updatedBanner.setTitle(bannerDetails.getTitle());
            updatedBanner.setImage(bannerDetails.getImage());
            updatedBanner.setPriority(bannerDetails.getPriority());
            updatedBanner.setStatus(bannerDetails.getStatus());
            updatedBanner.setLink(bannerDetails.getLink());
            updatedBanner.setPosition(bannerDetails.getPosition());
            return bannerRepository.save(updatedBanner);
        }
        return null;
    }

    // Xóa banner
    public void deleteBanner(Long id) {
        bannerRepository.deleteById(id);
    }

    // Lưu banner
    public void save(Banner banner) {
        bannerRepository.save(banner);
    }

    // Lấy danh sách banner phân trang
    public Page<Banner> findAll(Pageable pageable) {
        return bannerRepository.findAll(pageable);
    }
}