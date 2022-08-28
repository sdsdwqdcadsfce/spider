package com.peait.spider.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
@Data
public class DfStockDetail {
    private String id;

    private String stockCode;

    private String stockName;

    private String stockDetailUrl;

    private BigDecimal stockPrice;

    private String fluctuationRatio;

    private String turnoverRatio;

    private BigDecimal stockPe;

    private BigDecimal stockPbr;

    private String stockIndustry1;

    private Date dataDate;


}