package com.agri.market.cloudinary.service;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {

    String uploadProductImage(
            MultipartFile file
    );

    void deleteImage(
            String publicId
    );
}