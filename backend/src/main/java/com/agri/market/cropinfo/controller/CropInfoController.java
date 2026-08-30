package com.agri.market.cropinfo.controller;

import com.agri.market.cropinfo.dto.CropInfoResponseDto;
import com.agri.market.cropinfo.dto.CropSearchResponseDto;
import com.agri.market.cropinfo.dto.CropSummaryDto;
import com.agri.market.cropinfo.service.CropInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/crops/info")
@RequiredArgsConstructor
@Validated
@Tag(name = "Crop Information", description = "Crop information and search APIs")
public class CropInfoController {

    private final CropInfoService cropInfoService;

    @GetMapping
    @Operation(
            summary = "Get featured crops",
            description = "Returns crop summaries available in the database"
    )
    public ResponseEntity<List<CropSummaryDto>> getFeaturedCrops() {
        return ResponseEntity.ok(cropInfoService.getFeaturedCrops());
    }

    @GetMapping("/search")
    @Operation(
            summary = "Search crop information",
            description = "Searches for crop information using the database first and the external provider when required"
    )
    public ResponseEntity<CropSearchResponseDto> searchCrop(
            @Parameter(
                    description = "Crop name to search",
                    example = "Tomato",
                    required = true
            )
            @RequestParam
            @NotBlank(message = "VALIDATION.CROP_INFO.SEARCH.BLANK")
            @Size(
                    max = 100,
                    message = "VALIDATION.CROP_INFO.SEARCH.SIZE"
            )
            String query
    ) {
        return ResponseEntity.ok(cropInfoService.searchCrop(query));
    }

    @GetMapping("/{cropId}")
    @Operation(
            summary = "Get crop details",
            description = "Returns detailed crop information"
    )
    public ResponseEntity<CropInfoResponseDto> getCropDetails(
            @Parameter(
                    description = "Crop information ID",
                    example = "38b5459e-f2de-4f86-be60-f0bd926020ec",
                    required = true
            )
            @PathVariable
            @NotBlank(message = "VALIDATION.CROP_INFO.ID.BLANK")
            String cropId
    ) {
        return ResponseEntity.ok(cropInfoService.getCropDetails(cropId));
    }
}