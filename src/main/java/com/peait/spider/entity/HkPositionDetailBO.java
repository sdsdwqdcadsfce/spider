package com.peait.spider.entity;

public class HkPositionDetailBO {
    private String stockCode;
    private String stockName;
    private Double stockNum;
    private Double stockPer;
    private String dataUrl;

    public String getStockCode() {
        return stockCode;
    }

    public String getStockName() {
        return stockName;
    }

    public Double getStockNum() {
        return stockNum;
    }

    public Double getStockPer() {
        return stockPer;
    }

    public String getDataUrl() {
        return dataUrl;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public void setStockNum(Double stockNum) {
        this.stockNum = stockNum;
    }

    public void setStockPer(Double stockPer) {
        this.stockPer = stockPer;
    }

    public void setDataUrl(String dataUrl) {
        this.dataUrl = dataUrl;
    }
}
