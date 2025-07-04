package com.bkap.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UpdateUserDto {
    private Long id;

    private String email;

    @NotBlank(message = "Name is required")
    @Size(min = 6, max = 20, message = "Name must be between 6 and 20 characters")
    private String name;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "\\d{10,13}", message = "Phone must be numeric and between 10-13 digits")
    private String phone;

    @NotBlank(message = "Address is required")
    private String address;

    // Getter và Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
