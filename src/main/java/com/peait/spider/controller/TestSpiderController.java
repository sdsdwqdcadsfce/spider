package com.peait.spider.controller;

import com.peait.spider.entity.HkPositionList;
import com.peait.spider.mapper.HkPositionBuyMapper;
import com.peait.spider.mapper.HkPositionListMapper;
import com.peait.spider.result.Result;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class TestSpiderController {
    @Resource
    private HkPositionListMapper hkPositionListMapper;
    @Resource
    private BuySchedule buySchedule;
    @Autowired
    private DfPageListSchedule dfPageListSchedule;
    @Autowired
    private DfPageDetailSchedule dfPageDetailSchedule;
    @Resource
    private HkPositionBuyMapper hkPositionBuyMapper;
    @Autowired
    private DfStockDetailSchedule dfStockDetailSchedule;

    @Autowired
    private GJDistrictDetailSchedule gjDistrictDetailSchedule;

    @RequestMapping("/area")
    public Result getAreaData() throws InterruptedException {
        gjDistrictDetailSchedule.scheduler();
        return Result.success("成功");
    }

    @RequestMapping("/ping")
    public Result getPing(){
        return Result.success("成功");
    }
    @RequestMapping("/detail")
    public Result getDetail() throws InterruptedException {
        dfPageDetailSchedule.getDataDetail();
        return Result.success("成功");
    }

    @RequestMapping("/list")
    public Result getList() throws InterruptedException {
        dfPageListSchedule.scheduler();
        return Result.success("成功");
    }
    @RequestMapping("/buy/schedule")
    public Result getBuySchedule() throws InterruptedException {
        buySchedule.getDataDetail();
        return Result.success("成功");
    }
    @RequestMapping("/data")
    public Result getData(){
        List<HashMap> dataMap = hkPositionListMapper.getData();
        return Result.success(dataMap);
    }

    @RequestMapping("/buy")
    public Result getbuy(){
        List<HashMap> hashMaps = hkPositionBuyMapper.selectData();
        return Result.success(hashMaps);
    }
    @RequestMapping("/stock")
    public Result getStock() throws InterruptedException {
        dfStockDetailSchedule.scheduler();
        return Result.success("成功");
    }

//    @RequestMapping("/list")
//    public Result getList() throws InterruptedException {
//        dfPageListSchedule.scheduler();
//        return Result.success("成功");
//    }

//    @RequestMapping("/test")
    public Result spider() throws InterruptedException {
        //获取文件路径 请求的文件地址 爬取的表格小path 爬取的数据xpath
        HashMap<String,String> dataMap = hkPositionListMapper.selectAddressDataById("hk_position_list");
        String queryUrl = dataMap.get("queryUrl");
        LocalDate now = LocalDate.now();
        LocalDate yestoday = now.minusDays(1);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String yesdayStr = yestoday.format(dateTimeFormatter);
        queryUrl = queryUrl.replace("{date}",yesdayStr);
        System.setProperty("webdriver.chrome.driver", dataMap.get("chromeUrl"));
        ChromeOptions chromeOptions=new ChromeOptions();
        chromeOptions.addArguments("-headless");
        chromeOptions.addArguments("no-sandbox");
        String actionXPath = dataMap.get("actionXPath");
        WebDriver webDriver = new ChromeDriver(chromeOptions);
        webDriver.get(queryUrl);
        String tableXpath = dataMap.get("tableXpath");
        //点击下一页链接
        WebElement next = ((ChromeDriver) webDriver).findElementByXPath(actionXPath);
        String text = next.getText();
        webDriver.close();
      return Result.success(text);
    }


}
