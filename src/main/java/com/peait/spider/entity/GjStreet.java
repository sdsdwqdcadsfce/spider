package com.peait.spider.entity;

import lombok.Data;

@Data
public class GjStreet {
    private String streetId;

    private String districtId;

    private String streetName;

    private Integer updateStatus;

    private String queryUrl;


}