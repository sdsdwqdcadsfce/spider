package com.peait.spider.entity;

import java.util.Date;

public class ExcelMacrRead {
    private String id;

    private String dbCity;

    private String dbDate;

    private String firstType;

    private String secondType;

    private String thirdType;

    private String fourthType;

    private String dataValue;

    private String dataIndex;

    private String atachementContentid;

    private Integer delete;

    private Date businessDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getDbCity() {
        return dbCity;
    }

    public void setDbCity(String dbCity) {
        this.dbCity = dbCity == null ? null : dbCity.trim();
    }

    public String getDbDate() {
        return dbDate;
    }

    public void setDbDate(String dbDate) {
        this.dbDate = dbDate == null ? null : dbDate.trim();
    }

    public String getFirstType() {
        return firstType;
    }

    public void setFirstType(String firstType) {
        this.firstType = firstType == null ? null : firstType.trim();
    }

    public String getSecondType() {
        return secondType;
    }

    public void setSecondType(String secondType) {
        this.secondType = secondType == null ? null : secondType.trim();
    }

    public String getThirdType() {
        return thirdType;
    }

    public void setThirdType(String thirdType) {
        this.thirdType = thirdType == null ? null : thirdType.trim();
    }

    public String getFourthType() {
        return fourthType;
    }

    public void setFourthType(String fourthType) {
        this.fourthType = fourthType == null ? null : fourthType.trim();
    }

    public String getDataValue() {
        return dataValue;
    }

    public void setDataValue(String dataValue) {
        this.dataValue = dataValue == null ? null : dataValue.trim();
    }

    public String getDataIndex() {
        return dataIndex;
    }

    public void setDataIndex(String dataIndex) {
        this.dataIndex = dataIndex == null ? null : dataIndex.trim();
    }

    public String getAtachementContentid() {
        return atachementContentid;
    }

    public void setAtachementContentid(String atachementContentid) {
        this.atachementContentid = atachementContentid == null ? null : atachementContentid.trim();
    }

    public Integer getDelete() {
        return delete;
    }

    public void setDelete(Integer delete) {
        this.delete = delete;
    }

    public Date getBusinessDate() {
        return businessDate;
    }

    public void setBusinessDate(Date businessDate) {
        this.businessDate = businessDate;
    }
}