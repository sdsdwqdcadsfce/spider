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
import java.util.concurrent.TimeUnit;

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
//        chromeUrl = "C:\\Users\\10040\\AppData\\Local\\Google\\Chrome\\Application\\chromedriver.exe";
        System.getProperties().setProperty("webdriver.chrome.driver", chromeUrl);
//        System.getProperties().setProperty("webdriver.chrome.driver", dataMap.get("win_chrome_url"));
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("-headless");
        chromeOptions.addArguments("no-sandbox");
        ChromeDriver webDriver = new ChromeDriver(chromeOptions);
        webDriver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
        webDriver.get(queryUrl);
        try {
            Thread.sleep(1000);
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
        WebElement tableElement = driver.findElement(By.xpath(tableXpath));
        List<WebElement> tr = tableElement.findElements(By.xpath("tr"));
        //省列表
        for (int i = 0; i < tr.size(); i++) {
            WebElement webElement = tr.get(i);
            log.error("获取到的class内容" + webElement.getAttribute("class"));
            if (webElement.getAttribute("class").equals("provincetr")) {
                List<WebElement> tdElement = webElement.findElements(By.xpath("td"));
                for (WebElement provinceElement : tdElement) {
                    WebElement provinceA = provinceElement.findElement(By.xpath("a"));
                    String provinceHref = provinceA.getAttribute("href");
                    String provinceName = StringUtils.replace(provinceA.getText(), "\"", "");
                    //新增省的数据
                    GjProvince gjProvince = new GjProvince();
                    gjProvince.setProvinceId(provinceName);
                    gjProvince.setProvinceName(provinceName);
                    gjProvince.setQueryUrl(provinceHref);
                    gjProvince.setUpdateStatus(0);
                    GjProvince gjProvince1 = gjProvinceMapper.selectByPrimaryKey(provinceName);
                    if(gjProvince1==null || gjProvince1.getUpdateStatus()==0){
                        gjProvinceMapper.deleteByPrimaryKey(provinceName);
                        gjProvinceMapper.insertSelective(gjProvince);
                    }
                }
            }

        }
        closeWebDriver(driver);
        //省结束，开始市区的查询
        List<GjProvince> gjProvinces = gjProvinceMapper.selectByUpdateStatus();
        queryCityData(gjProvinces, chromeUrl);
    }

    private void queryCityData(List<GjProvince> gjProvinces, String chromeUrl) {
        for (GjProvince gjProvince : gjProvinces) {
            WebDriver webDriver = this.openWebDriver(chromeUrl, gjProvince.getQueryUrl());
            WebElement table = webDriver.findElement(By.xpath("/html/body/table[2]/tbody/tr[1]/td/table/tbody/tr[2]/td/table/tbody/tr/td/table/tbody"));
            List<WebElement> trList = table.findElements(By.xpath("tr"));
            for (WebElement tr : trList) {
                if (tr.getAttribute("class").equals("citytr")) {
                    List<WebElement> tdList = tr.findElements(By.xpath("td"));
                    String cityHref = tdList.get(0).findElement(By.xpath("a")).getAttribute("href");
                    String cityCode = tdList.get(0).findElement(By.xpath("a")).getText();
                    String cityName = tdList.get(1).findElement(By.xpath("a")).getText();
                    GjCity gjCity = new GjCity();
                    gjCity.setProvinceId(gjProvince.getProvinceId());
                    gjCity.setCityId(cityCode);
                    gjCity.setCityName(cityName);
                    gjCity.setQueryUrl(cityHref);
                    gjCity.setUpdateStatus(0);
                    GjCity gjCity1 = gjCityMapper.selectByPrimaryKey(cityCode);
                    if(gjCity1==null || gjCity1.getUpdateStatus()==0){
                        gjCityMapper.deleteByPrimaryKey(cityCode);
                        gjCityMapper.insertSelective(gjCity);
                    }
                }
            }
            closeWebDriver(webDriver);
            gjProvince.setUpdateStatus(1);
            gjProvinceMapper.updateByPrimaryKeySelective(gjProvince);
        }
        //市结束，开始区
        List<GjCity> gjCities = gjCityMapper.selectByUpdateStatus();
        queryDistrictData(gjCities, chromeUrl);
    }

    private void queryDistrictData( List<GjCity> gjCities , String chromeUrl) {
        for (GjCity gjCity : gjCities) {
            WebDriver webDriver = this.openWebDriver(chromeUrl, gjCity.getQueryUrl());
            WebElement table = webDriver.findElement(By.xpath("/html/body/table[2]/tbody/tr[1]/td/table/tbody/tr[2]/td/table/tbody/tr/td/table/tbody"));
            List<WebElement> trList = table.findElements(By.xpath("tr"));
            for (WebElement tr : trList) {
                if (tr.getAttribute("class").equals("countytr")) {
                    List<WebElement> tdList = tr.findElements(By.xpath("td"));
                    try {
                        String districtHref = tdList.get(0).findElement(By.xpath("a")).getAttribute("href");
                        String districtCode = tdList.get(0).findElement(By.xpath("a")).getText();
                        String districtName = tdList.get(1).findElement(By.xpath("a")).getText();
                        GjDistrict gjDistrict = new GjDistrict();
                        gjDistrict.setCityId(gjCity.getCityId());
                        gjDistrict.setDistrictId(districtCode);
                        gjDistrict.setDistrictName(districtName);
                        gjDistrict.setQueryUrl(districtHref);
                        gjDistrict.setUpdateStatus(0);
                        GjDistrict gjDistrict1 = gjDistrictMapper.selectByPrimaryKey(districtCode);
                        if(gjDistrict1==null || gjDistrict1.getUpdateStatus()==0){
                            gjDistrictMapper.deleteByPrimaryKey(districtCode);
                            gjDistrictMapper.insertSelective(gjDistrict);
                        }
                    } catch (Exception e) {
                        GjDistrict gjDistrict = new GjDistrict();
                        gjDistrict.setCityId(gjCity.getCityId());
                        gjDistrict.setDistrictId(tdList.get(0).getText());
                        gjDistrict.setDistrictName(tdList.get(1).getText());
                        gjDistrict.setQueryUrl("");
                        gjDistrict.setUpdateStatus(1);
                        gjDistrictMapper.deleteByPrimaryKey(tdList.get(0).getText());
                        gjDistrictMapper.insertSelective(gjDistrict);
                    }

                }
            }
            closeWebDriver(webDriver);
            gjCity.setUpdateStatus(1);
            gjCityMapper.updateByPrimaryKeySelective(gjCity);
        }
        //区结束，开始街道
        List<GjDistrict> districts = gjDistrictMapper.selectByUpdateStatus();
        queryStreetData(districts, chromeUrl);
    }

    private void queryStreetData( List<GjDistrict>  districts, String chromeUrl) {
        for (GjDistrict gjDistrict : districts) {
            WebDriver webDriver = this.openWebDriver(chromeUrl, gjDistrict.getQueryUrl());
            WebElement table = webDriver.findElement(By.xpath("/html/body/table[2]/tbody/tr[1]/td/table/tbody/tr[2]/td/table/tbody/tr/td/table/tbody"));
            List<WebElement> trList = table.findElements(By.xpath("tr"));
            for (WebElement tr : trList) {
                if (tr.getAttribute("class").equals("towntr")) {
                    List<WebElement> tdList = tr.findElements(By.xpath("td"));
                    String streetHref = tdList.get(0).findElement(By.xpath("a")).getAttribute("href");
                    String streetCode = tdList.get(0).findElement(By.xpath("a")).getText();
                    String streetName = tdList.get(1).findElement(By.xpath("a")).getText();
                    GjStreet gjStreet = new GjStreet();
                    gjStreet.setDistrictId(gjDistrict.getDistrictId());
                    gjStreet.setStreetId(streetCode);
                    gjStreet.setStreetName(streetName);
                    gjStreet.setQueryUrl(streetHref);
                    gjStreet.setUpdateStatus(0);
                    GjStreet gjStreet1 = gjStreetMapper.selectByPrimaryKey(streetCode);
                    if(gjStreet1==null || gjStreet1.getUpdateStatus()==0){
                        gjStreetMapper.deleteByPrimaryKey(streetCode);
                        gjStreetMapper.insertSelective(gjStreet);
                    }
                }
            }
            closeWebDriver(webDriver);
            gjDistrict.setUpdateStatus(1);
            gjDistrictMapper.updateByPrimaryKeySelective(gjDistrict);
        }
        //街道结束，开始居委会
        List<GjStreet> streets = gjStreetMapper.selectByUpdateStatus();
        queryVillagetrData(streets, chromeUrl);
    }

    private void queryVillagetrData(List<GjStreet> streets, String chromeUrl) {

        for (GjStreet gjStreet : streets) {
            WebDriver webDriver = this.openWebDriver(chromeUrl, gjStreet.getQueryUrl());
            WebElement table = webDriver.findElement(By.xpath("/html/body/table[2]/tbody/tr[1]/td/table/tbody/tr[2]/td/table/tbody/tr/td/table/tbody"));
            List<WebElement> trList = table.findElements(By.xpath("tr"));
            for (WebElement tr : trList) {
                if (tr.getAttribute("class").equals("villagetr")) {
                    List<WebElement> tdList = tr.findElements(By.xpath("td"));
                    String villId = tdList.get(0).getText();
                    String villTypeCode = tdList.get(1).getText();
                    String villName = tdList.get(2).getText();
                    GjVillagetr gjVillagetr = new GjVillagetr();
                    gjVillagetr.setStreetId(gjStreet.getStreetId());
                    gjVillagetr.setVillagetrId(villId);
                    gjVillagetr.setVillagetrTypeCode(villTypeCode);
                    gjVillagetr.setVillagetrName(villName);
                    gjVillagetrMapper.deleteByPrimaryKey(villId);
                    gjVillagetrMapper.insertSelective(gjVillagetr);
                }
            }
            gjStreet.setUpdateStatus(1);
            gjStreetMapper.updateByPrimaryKeySelective(gjStreet);
            closeWebDriver(webDriver);
        }


    }


}
