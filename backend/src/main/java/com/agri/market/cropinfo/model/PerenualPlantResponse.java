package com.agri.market.cropinfo.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerenualPlantResponse {

    private Integer id;
    private String common_name;
    private List<String> scientific_name;
    private List<String> other_name;
    private String cycle;
    private String watering;
    private List<String> sunlight;
    private List<String> soil;
    private String growth_rate;
    private String harvest_season;
    private String harvest_method;
    private List<String> pest_susceptibility;
    private String description;
    private PerenualImage default_image;
}