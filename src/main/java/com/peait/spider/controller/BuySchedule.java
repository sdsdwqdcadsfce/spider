package com.peait.spider.controller;

import com.peait.spider.entity.HkPositionBuy;
import com.peait.spider.entity.HkPositionList;
import com.peait.spider.entity.HkPostionDetail;
import com.peait.spider.mapper.HkPositionBuyMapper;
import com.peait.spider.mapper.HkPositionListMapper;
import com.peait.spider.mapper.HkPostionDetailMapper;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@EnableScheduling
@Slf4j
public class BuySchedule {

    @Resource
    private HkPositionListMapper hkPositionListMapper;
    @Resource
    private HkPostionDetailMapper hkPostionDetailMapper;
    @Resource
    private HkPositionBuyMapper hkPositionBuyMapper;

//    @Scheduled(cron = "0 0 18 ? * 1,2,3,4,5")
//    @Scheduled(cron = "0 */1 * * * ?")
    public void getDataDetail() throws InterruptedException {
        List<HashMap> dataMap = hkPositionListMapper.getData();
        for (int i = 0; i < dataMap.size(); i++) {
            HkPositionBuy hkPositionBuy = new HkPositionBuy();
            hkPositionBuy.setBuyDate(new Date());
            hkPositionBuy.setBuyPrice(Double.parseDouble(String.valueOf(dataMap.get(i).get("price"))));
            hkPositionBuy.setStockCode(String.valueOf(dataMap.get(i).get("stockCode")));
            hkPositionBuy.setStockName(String.valueOf(dataMap.get(i).get("stockName")));
            hkPositionBuyMapper.insertSelective(hkPositionBuy);
        }
    }

}
