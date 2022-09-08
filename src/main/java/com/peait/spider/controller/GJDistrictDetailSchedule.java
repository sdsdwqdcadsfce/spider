package com.peait.spider.controller;

import com.peait.spider.entity.*;
import com.peait.spider.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@EnableScheduling
@Slf4j
public class GJDistrictDetailSchedule {
    @Resource
    private HkPositionListMapper hkPositionListMapper;
    @Resource
    private DfStockDetailMapper dfStockDetailMapper;
    @Resource
    private GjProvinceMapper gjProvinceMapper;

    @Resource
    private GjCityMapper gjCityMapper;

    @Resource
    private GjDistrictMapper gjDistrictMapper;

    @Resource
    private GjStreetMapper gjStreetMapper;

    @Resource
    private GjVillagetrMapper gjVillagetrMapper;

    //    @Scheduled(cron = "0 0 1 ? * 6 ")
//    @Scheduled(cron = "0 0 20 ? * *")
    public void scheduler() throws InterruptedException {
        //获取文件路径 请求的文件地址 爬取的表格小path 爬取的数据xpath
        log.info("开始进入");
        HashMap<String, String> dataMap = hkPositionListMapper.selectAddressDataById("gj_area_detail");
        String chromeUrl = dataMap.get("chromeUrl");
        String queryUrl = dataMap.get("queryUrl");
        WebDriver webDriver = this.openWebDriver(chromeUrl, queryUrl);
        String tableXpath = dataMap.get("tableXpath");
        String actionXPath = dataMap.get("actionXPath");
        log.error(" //开始循环查询");
        int i = 0;
        try {
            spiderData(webDriver, chromeUrl, tableXpath, actionXPath, i);
        } catch (InterruptedException e) {
            log.error("程序异常退出");
        }
        log.error("程序退出");
        closeWebDriver(webDriver);
    }

    public WebDriver openWebDriver(String chromeUrl, String queryUrl) {
        System.getProperties().setProperty("webdriver.chrome.driver", chromeUrl);
//        System.getProperties().setProperty("webdriver.chrome.driver", dataMap.get("win_chrome_url"));
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("-headless");
        chromeOptions.addArguments("no-sandbox");
        ChromeDriver webDriver = new ChromeDriver(chromeOptions);
        webDriver.get(queryUrl);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return webDriver;
    }

    public void closeWebDriver(WebDriver webDriver) {
        log.error("程序退出");
        webDriver.close();
        webDriver.quit();
    }

    private void spiderData(WebDriver driver, String chromeUrl, String tableXpath, String actionXPath, int numCount) throws InterruptedException {
        Thread.sleep(10000);
        WebElement tableElement = driver.findElement(By.xpath(tableXpath));
        List<WebElement> tr = tableElement.findElements(By.xpath("tr"));
        //省列表
        LinkedHashMap<String, String> provinceMap = new LinkedHashMap<>();
        for (int i = 0; i < tr.size(); i++) {
            WebElement webElement = tr.get(i);
            log.error("获取到的class内容" + webElement.getAttribute("class"));
            if (webElement.getAttribute("class").equals("provincetr")) {
                List<WebElement> tdElement = webElement.findElements(By.xpath("td"));
                for (WebElement provinceElement : tdElement) {
                    WebElement provinceA = provinceElement.findElement(By.xpath("a"));
                    String provinceHref = provinceA.getAttribute("href");
                    String provinceName = StringUtils.replace(provinceA.getText(), "\"", "");
                    provinceMap.put(provinceHref, provinceName);
                    //新增省的数据
                    GjProvince gjProvince = new GjProvince();
                    gjProvince.setProvinceId(provinceName);
                    gjProvince.setProvinceName(provinceName);
                    gjProvinceMapper.insertSelective(gjProvince);
                }
            }

        }
        closeWebDriver(driver);
        //省结束，开始市区的查询
        queryCityData(provinceMap, chromeUrl);


    }

    private void queryCityData(LinkedHashMap<String, String> provinceMap, String chromeUrl) {
        Set<String> provinceHrefSet = provinceMap.keySet();
        //初始化一个citylist
        LinkedHashMap<String, String> cityMap = new LinkedHashMap<>();
        for (String provinceHref : provinceHrefSet) {
            WebDriver webDriver = this.openWebDriver(chromeUrl, provinceHref);
            WebElement table = webDriver.findElement(By.xpath("/html/body/table[2]/tbody/tr[1]/td/table/tbody/tr[2]/td/table/tbody/tr/td/table/tbody"));
            List<WebElement> trList = table.findElements(By.xpath("tr"));
            for (WebElement tr : trList) {
                if (tr.getAttribute("class").equals("citytr")) {
                    List<WebElement> tdList = tr.findElements(By.xpath("td"));
                    String cityHref = tdList.get(0).findElement(By.xpath("a")).getAttribute("href");
                    String cityCode = tdList.get(0).findElement(By.xpath("a")).getText();
                    String cityName = tdList.get(1).findElement(By.xpath("a")).getText();
                    GjCity gjCity = new GjCity();
                    gjCity.setProvinceId(provinceMap.get(provinceHref));
                    gjCity.setCityId(cityCode);
                    gjCity.setCityName(cityName);
                    gjCityMapper.insertSelective(gjCity);
                    cityMap.put(cityHref, cityCode);
                }
            }
            closeWebDriver(webDriver);
        }
        //市结束，开始区
        queryDistrictData(cityMap, chromeUrl);
    }

    private void queryDistrictData(LinkedHashMap<String, String> cityMap, String chromeUrl) {
        Set<String> cityHrefSet = cityMap.keySet();
        //初始化一个citylist
        LinkedHashMap<String, String> districtMap = new LinkedHashMap<>();
        for (String cityHref : cityHrefSet) {
            WebDriver webDriver = this.openWebDriver(chromeUrl, cityHref);
            WebElement table = webDriver.findElement(By.xpath("/html/body/table[2]/tbody/tr[1]/td/table/tbody/tr[2]/td/table/tbody/tr/td/table/tbody"));
            List<WebElement> trList = table.findElements(By.xpath("tr"));
            for (WebElement tr : trList) {
                if (tr.getAttribute("class").equals("countytr")) {
                    List<WebElement> tdList = tr.findElements(By.xpath("td"));
                    if (tdList.get(0).findElement(By.xpath("a")) == null || tdList.get(0).findElement(By.xpath("a")).getText() == null) {
                        GjDistrict gjDistrict = new GjDistrict();
                        gjDistrict.setCityId(cityMap.get(cityHref));
                        gjDistrict.setDistrictId(tdList.get(0).getText());
                        gjDistrict.setDistrictName(tdList.get(1).getText());
                        gjDistrictMapper.insertSelective(gjDistrict);
                        continue;
                    } else {
                        String districtHref = tdList.get(0).findElement(By.xpath("a")).getAttribute("href");
                        String districtCode = tdList.get(0).findElement(By.xpath("a")).getText();
                        String districtName = tdList.get(1).findElement(By.xpath("a")).getText();
                        GjDistrict gjDistrict = new GjDistrict();
                        gjDistrict.setCityId(cityMap.get(cityHref));
                        gjDistrict.setDistrictId(districtCode);
                        gjDistrict.setDistrictName(districtName);
                        districtMap.put(districtHref, districtCode);
                        gjDistrictMapper.insertSelective(gjDistrict);
                    }
                }
            }
            closeWebDriver(webDriver);
        }
        //区结束，开始街道
        queryStreetData(districtMap, chromeUrl);
    }

    private void queryStreetData(LinkedHashMap<String, String> districtMap, String chromeUrl) {
        Set<String> districtHrefSet = districtMap.keySet();
        //初始化一个citylist
        LinkedHashMap<String, String> streetMap = new LinkedHashMap<>();
        for (String districtHref : districtHrefSet) {
            WebDriver webDriver = this.openWebDriver(chromeUrl, districtHref);
            WebElement table = webDriver.findElement(By.xpath("/html/body/table[2]/tbody/tr[1]/td/table/tbody/tr[2]/td/table/tbody/tr/td/table/tbody"));
            List<WebElement> trList = table.findElements(By.xpath("tr"));
            for (WebElement tr : trList) {
                if (tr.getAttribute("class").equals("towntr")) {
                    List<WebElement> tdList = tr.findElements(By.xpath("td"));
                    String streetHref = tdList.get(0).findElement(By.xpath("a")).getAttribute("href");
                    String streetCode = tdList.get(0).findElement(By.xpath("a")).getText();
                    String streetName = tdList.get(1).findElement(By.xpath("a")).getText();
                    GjStreet gjStreet = new GjStreet();
                    gjStreet.setDistrictId(districtMap.get(districtHref));
                    gjStreet.setStreetId(streetCode);
                    gjStreet.setStreetName(streetName);
                    gjStreetMapper.insertSelective(gjStreet);
                    streetMap.put(streetHref, streetCode);
                }
            }
            closeWebDriver(webDriver);
        }
        //街道结束，开始居委会
        queryVillagetrData(streetMap, chromeUrl);
    }

    private void queryVillagetrData(LinkedHashMap<String, String> streetMap, String chromeUrl) {
        Set<String> streetHrefSet = streetMap.keySet();
        //初始化一个citylist
        for (String streetHref : streetHrefSet) {
            WebDriver webDriver = this.openWebDriver(chromeUrl, streetHref);
            WebElement table = webDriver.findElement(By.xpath("/html/body/table[2]/tbody/tr[1]/td/table/tbody/tr[2]/td/table/tbody/tr/td/table/tbody"));
            List<WebElement> trList = table.findElements(By.xpath("tr"));
            for (WebElement tr : trList) {
                if (tr.getAttribute("class").equals("villagetr")) {
                    List<WebElement> tdList = tr.findElements(By.xpath("td"));
                    String villId = tdList.get(0).findElement(By.xpath("a")).getText();
                    String villTypeCode = tdList.get(1).findElement(By.xpath("a")).getText();
                    String villName = tdList.get(2).findElement(By.xpath("a")).getText();
                    GjVillagetr gjVillagetr = new GjVillagetr();
                    gjVillagetr.setStreetId(streetMap.get(streetHref));
                    gjVillagetr.setVillagetrId(villId);
                    gjVillagetr.setVillagetrTypeCode(villTypeCode);
                    gjVillagetr.setVillagetrName(villName);
                    gjVillagetrMapper.insertSelective(gjVillagetr);
                }
            }
            closeWebDriver(webDriver);
        }


    }


}
