package com.agri.market.category.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Category information")
public class CategoryResponseDto {

    @JsonProperty("id")
    @Schema(
            description = "Unique identifier of the category",
            example = "550e8400-e29b-41d4-a716-446655440001"
    )
    private String id;

    @JsonProperty("name")
    @Schema(
            description = "Category name",
            example = "Seeds"
    )
    private String name;
}