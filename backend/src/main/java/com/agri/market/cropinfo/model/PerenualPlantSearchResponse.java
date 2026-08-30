package com.agri.market.cropinfo.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerenualPlantSearchResponse {

    private List<PerenualPlantResponse> data;
    private Integer to;
    private Integer per_page;
    private Integer current_page;
    private Integer from;
    private Integer last_page;
    private Integer total;
}