package com.agri.market.cloudinary.service;

import com.agri.market.common.exception.BusinessException;
import com.agri.market.common.exception.ErrorCode;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryServiceImpl
        implements CloudinaryService {

    private static final String PRODUCT_IMAGE_FOLDER =
            "agrimarket/products";
    private static final String PROFILE_PICTURE_FOLDER =
            "agrimarket/profile-pictures";
    private static final long MAX_PROFILE_PICTURE_SIZE =
            5 * 1024 * 1024;

    private final Cloudinary cloudinary;

    @Override
    public String uploadProductImage(
            final MultipartFile file
    ) {

        if (file == null || file.isEmpty()) {

            log.warn(
                    "Product image upload rejected because file is empty"
            );

            throw new BusinessException(
                    ErrorCode.INVALID_PRODUCT_IMAGE
            );
        }

        try {

            final Map<?, ?> uploadResult =
                    cloudinary.uploader().upload(
                            file.getBytes(),
                            ObjectUtils.asMap(
                                    "folder",
                                    PRODUCT_IMAGE_FOLDER,
                                    "resource_type",
                                    "image"
                            )
                    );

            final String secureUrl =
                    (String) uploadResult.get("secure_url");

            if (secureUrl == null
                    || secureUrl.isBlank()) {

                log.error(
                        "Cloudinary upload completed but secure URL was not returned"
                );

                throw new BusinessException(
                        ErrorCode.PRODUCT_IMAGE_UPLOAD_FAILED
                );
            }

            log.info(
                    "Product image uploaded successfully to Cloudinary"
            );

            return secureUrl;

        } catch (IOException exception) {

            log.error(
                    "Failed to upload product image to Cloudinary",
                    exception
            );

            throw new BusinessException(
                    ErrorCode.PRODUCT_IMAGE_UPLOAD_FAILED
            );
        }
    }

    @Override
    public String uploadProfilePicture(
            final MultipartFile file
    ) {

        validateProfilePicture(file);

        try {

            final Map<?, ?> uploadResult =
                    cloudinary.uploader().upload(
                            file.getBytes(),
                            ObjectUtils.asMap(
                                    "folder",
                                    PROFILE_PICTURE_FOLDER,
                                    "resource_type",
                                    "image"
                            )
                    );

            final String secureUrl =
                    (String) uploadResult.get("secure_url");

            if (secureUrl == null
                    || secureUrl.isBlank()) {

                log.error(
                        "Cloudinary profile picture upload completed but secure URL was not returned"
                );

                throw new BusinessException(
                        ErrorCode.PROFILE_PICTURE_UPLOAD_FAILED
                );
            }

            log.info(
                    "Profile picture uploaded successfully to Cloudinary"
            );

            return secureUrl;

        } catch (IOException exception) {

            log.error(
                    "Failed to upload profile picture to Cloudinary",
                    exception
            );

            throw new BusinessException(
                    ErrorCode.PROFILE_PICTURE_UPLOAD_FAILED
            );
        }
    }


    @Override
    public void deleteImage(
            final String publicId
    ) {

        if (publicId == null
                || publicId.isBlank()) {

            log.warn(
                    "Cloudinary image deletion skipped because public ID is missing"
            );

            return;
        }

        try {

            cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.asMap(
                            "resource_type",
                            "image"
                    )
            );

            log.info(
                    "Product image deleted successfully from Cloudinary. Public ID: {}",
                    publicId
            );

        } catch (IOException exception) {

            log.error(
                    "Failed to delete product image from Cloudinary. Public ID: {}",
                    publicId,
                    exception
            );

            throw new BusinessException(
                    ErrorCode.PRODUCT_IMAGE_DELETE_FAILED
            );
        }
    }

    private void validateProfilePicture(
            final MultipartFile file
    ) {

        if (file == null || file.isEmpty()) {

            log.warn(
                    "Profile picture upload rejected because file is empty"
            );

            throw new BusinessException(
                    ErrorCode.INVALID_PROFILE_PICTURE
            );
        }

        if (file.getSize() > MAX_PROFILE_PICTURE_SIZE) {

            log.warn(
                    "Profile picture upload rejected because file size exceeds 5 MB"
            );

            throw new BusinessException(
                    ErrorCode.INVALID_PROFILE_PICTURE
            );
        }

        final String contentType =
                file.getContentType();

        if (contentType == null
                || !isSupportedImageType(contentType)) {

            log.warn(
                    "Profile picture upload rejected because file type is unsupported: {}",
                    contentType
            );

            throw new BusinessException(
                    ErrorCode.INVALID_PROFILE_PICTURE
            );
        }
    }

    private boolean isSupportedImageType(
            final String contentType
    ) {

        return "image/jpeg".equalsIgnoreCase(contentType)
                || "image/png".equalsIgnoreCase(contentType)
                || "image/webp".equalsIgnoreCase(contentType);
    }
}