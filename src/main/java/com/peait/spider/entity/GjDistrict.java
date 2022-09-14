package com.peait.spider.entity;

import lombok.Data;

@Data
public class GjDistrict {
    private String districtId;

    private String cityId;

    private String districtName;

    private String queryUrl;

    private Integer updateStatus;


}