package com.agri.market.cropinfo.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerenualImage {

    private String original_url;
    private String regular_url;
    private String medium_url;
    private String small_url;
    private String thumbnail;
}