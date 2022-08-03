package com.peait.spider.controller;

import com.peait.spider.entity.HkPositionList;
import com.peait.spider.entity.HkPostionDetail;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@EnableScheduling
@Slf4j
public class DfPageDetailSchedule {
    @Resource
    private HkPositionListMapper hkPositionListMapper;
    @Resource
    private HkPostionDetailMapper hkPostionDetailMapper;

    @Scheduled(cron = "0 0 5 ? * 1,2,3,4,5")
    public void getDataDetail() throws InterruptedException {
        //获取列表 获取小于当天日期的30条数据
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String newDate = simpleDateFormat.format(new Date());
        int errorNUm = 0;
        boolean flag = true;
        while (flag && errorNUm <= 10) {
            List<HkPositionList> positionlIst = hkPostionDetailMapper.hkPositionListMapper(newDate);
            if (positionlIst == null || positionlIst.size() == 0) {
                flag = false;
            }
            if (positionlIst != null && positionlIst.size() > 0) {
                for (HkPositionList hkPosition : positionlIst) {
                    //初始化新增数据
                    List<HkPostionDetail> dbList = new ArrayList<>();

                    //获取数据库配置的表格xpath
                    HashMap<String, String> stringStringHashMap = hkPositionListMapper.selectAddressData();
                    WebDriver webDriver = null;
                    String[] lineList = new String[0];

                    //爬取数据
                    System.getProperties().setProperty("webdriver.chrome.driver", stringStringHashMap.get("chromeUrl"));
//                    System.getProperties().setProperty("webdriver.chrome.driver", stringStringHashMap.get("win_chrome_url"));
                    ChromeOptions chromeOptions = new ChromeOptions();
                    chromeOptions.addArguments("-headless");
                    chromeOptions.addArguments("no-sandbox");
                    webDriver = new ChromeDriver(chromeOptions);
                    webDriver.get(hkPosition.getDataUrl());
                    String tableXpath = stringStringHashMap.get("detailXpath");
                    Thread.sleep(12000);
                    WebElement tableElement = null;
                    try {
                        tableElement = webDriver.findElement(By.xpath(tableXpath));
                    } catch (Exception e) {
                        errorNUm++;
                        log.error("错误" + errorNUm + "次");
                        webDriver.close();
                        webDriver.quit();
                        continue;
                    }
                    //解析数据
                    String oldStr = tableElement.getText();
                    lineList = oldStr.split("\n");
                    try {
                        for (int i = 0; i < lineList.length; i++) {
                            String[] dataArr = lineList[i].split(" ");
                            String date = dataArr[0];
                            double positionNum = Double.parseDouble(datareplis(dataArr[4]));
                            double postionPer = Double.parseDouble(datareplis(dataArr[6]));
                            double stockPrice = 0;
                            double stockUp = 0;
                            try {
                                stockPrice = Double.parseDouble(datareplis(dataArr[2]));
                                stockUp = Double.parseDouble(datareplis(dataArr[3]));
                            } catch (Exception e) {
                                log.error("出错"+hkPosition.getDataUrl());
                            }
                            HkPostionDetail hkPostionDetail = new HkPostionDetail();
                            hkPostionDetail.setStockCode(hkPosition.getDataCode());
                            hkPostionDetail.setStockName(hkPosition.getDataName());
                            hkPostionDetail.setPostionDate(date);
                            hkPostionDetail.setStockNum(positionNum);
                            hkPostionDetail.setStockPer(postionPer);
                            hkPostionDetail.setStockPrice(stockPrice);
                            hkPostionDetail.setStockUp(stockUp);

                            dbList.add(hkPostionDetail);

                        }
                        webDriver.close();
                        webDriver.quit();
                        //获取该code有数据的天数
                        List<String> existDateList = hkPostionDetailMapper.selectDateByCode(hkPosition.getDataCode());
                        //过滤掉已有的日期，剩下的新增数据，
                        List<HkPostionDetail> insertList = dbList.stream().filter(x -> !existDateList.contains(x.getPostionDate())).collect(Collectors.toList());
                        for (HkPostionDetail db : insertList) {
                            hkPostionDetailMapper.insertSelective(db);
                        }
                        //列表的数据修改成当天
                        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
                        String nowDate = simpleDateFormat1.format(new Date());
                        hkPositionListMapper.updateByCode(hkPosition.getDataCode(), nowDate);
                    } catch (Exception e) {
                        errorNUm++;
                        webDriver.close();
                        webDriver.quit();
                        log.error("出错"+hkPosition.getDataUrl());
                    } finally {

                    }

                }
            } else {
                break;
            }
        }

    }

    private String datareplis(String oldData) {
        if (oldData.contains("万")) {
            oldData = oldData.replace("万", "");
            double value = Double.parseDouble(oldData);
            BigDecimal bigDecimal = new BigDecimal(oldData);
            BigDecimal bm2 = new BigDecimal("10000");
            BigDecimal total = bigDecimal.multiply(bm2).setScale(2, BigDecimal.ROUND_HALF_UP);
            return String.valueOf(total);
        } else if (oldData.contains("亿")) {
            oldData = oldData.replace("亿", "");
            double value = Double.parseDouble(oldData);
            BigDecimal bigDecimal = new BigDecimal(oldData);
            BigDecimal bm2 = new BigDecimal("100000000");
            BigDecimal total = bigDecimal.multiply(bm2).setScale(2, BigDecimal.ROUND_HALF_UP);
            return String.valueOf(total);
        }
        return oldData;
    }
}
