package com.agri.market.cropinfo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Crop image information")
public class CropImageDto {

    @Schema(description = "Crop image URL")
    private String imageUrl;
}