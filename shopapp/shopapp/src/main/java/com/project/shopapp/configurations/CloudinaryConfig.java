package com.project.shopapp.configurations;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {
    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "websitemart",
                "api_key", "412457167463546",
                "api_secret", "9MM2ghmvzm8u8hJ6aNDmKP7Ml0I"
        ));
    }
}
