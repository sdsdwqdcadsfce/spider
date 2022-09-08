package com.peait.spider.entity;

public class GjStreet {
    private String streetId;

    private String districtId;

    private String streetName;

    public String getStreetId() {
        return streetId;
    }

    public void setStreetId(String streetId) {
        this.streetId = streetId == null ? null : streetId.trim();
    }

    public String getDistrictId() {
        return districtId;
    }

    public void setDistrictId(String districtId) {
        this.districtId = districtId == null ? null : districtId.trim();
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName == null ? null : streetName.trim();
    }
}