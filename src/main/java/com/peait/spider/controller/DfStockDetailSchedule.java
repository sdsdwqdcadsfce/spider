package com.peait.spider.controller;

import com.peait.spider.entity.DfStockDetail;
import com.peait.spider.mapper.DfStockDetailMapper;
import com.peait.spider.mapper.HkPositionListMapper;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@EnableScheduling
@Slf4j
public class DfStockDetailSchedule {
    @Resource
    private HkPositionListMapper hkPositionListMapper;
    @Resource
    private DfStockDetailMapper dfStockDetailMapper;

    //    @Scheduled(cron = "0 0 1 ? * 6 ")
    @Scheduled(cron = "0 0/1 * * * ? ")
    public void scheduler() throws InterruptedException {
        //获取文件路径 请求的文件地址 爬取的表格小path 爬取的数据xpath
        log.info("开始进入");
        HashMap<String, String> dataMap = hkPositionListMapper.selectAddressDataById("df_stock_detail");
        String queryUrl = dataMap.get("queryUrl");
        System.getProperties().setProperty("webdriver.chrome.driver", dataMap.get("chromeUrl"));
//        System.getProperties().setProperty("webdriver.chrome.driver", dataMap.get("win_chrome_url"));
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("-headless");
        chromeOptions.addArguments("no-sandbox");
        WebDriver webDriver = new ChromeDriver(chromeOptions);
        webDriver.get(queryUrl);
        List<DfStockDetail> dataList = new ArrayList<>();
        String tableXpath = dataMap.get("tableXpath");
        String actionXPath = dataMap.get("actionXPath");
        log.error(" //开始循环查询");
        int i = 0;
        try {
            spiderData(dataList, webDriver, tableXpath, actionXPath, i);
        } catch (InterruptedException e) {
            log.error("程序异常退出");
        }
        log.error("程序退出");
        webDriver.close();
        webDriver.quit();

    }

    private void spiderData(List<DfStockDetail> dataList, WebDriver driver, String tableXpath, String actionXPath, int numCount) throws InterruptedException {
        Thread.sleep(10000);
        WebElement tableElement = driver.findElement(By.xpath(tableXpath));
        List<WebElement> tr = tableElement.findElements(By.xpath("tr"));
        for (int i = 0; i < tr.size(); i++) {
            List<WebElement> td = tr.get(i).findElements(By.xpath("td"));
            DfStockDetail dfStockDetail = new DfStockDetail();
            dfStockDetail.setStockCode(td.get(1).getText());
            dfStockDetail.setStockName(td.get(2).getText());
            dfStockDetail.setStockDetailUrl(td.get(2).findElements(By.xpath("a")).get(0).getAttribute("href"));
            try {
                dfStockDetail.setStockPrice(new BigDecimal(td.get(4).getText()));
            } catch (Exception e) {
                continue;
            }
            dfStockDetail.setFluctuationRatio(td.get(5).getText());
            dfStockDetail.setTurnoverRatio(td.get(14).getText());
            dfStockDetail.setStockPe(new BigDecimal(td.get(15).getText()));
            dfStockDetail.setStockPbr(new BigDecimal(td.get(16).getText()));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dfStockDetail.setId(simpleDateFormat.format(new Date())+dfStockDetail.getStockCode());
            dfStockDetail.setDataDate(new Date());
            try {
                dfStockDetailMapper.deleteByPrimaryKey(dfStockDetail.getId());
                dfStockDetailMapper.insertSelective(dfStockDetail);
            } catch (Exception e) {
                continue;
            }

        }
        //点击下一页链接
        WebElement next = ((ChromeDriver) driver).findElementByXPath(actionXPath);
        System.out.println(next.getText());
        System.out.println(next.getAttribute("class"));
        if(next.getText().equals("下一页") && !next.getAttribute("class").contains("disabled")){
            Actions actions = new Actions(driver);
            actions.moveToElement(next).click().perform();
            //点击完下一页 等待10秒
//            Thread.sleep(10000);
            String currentHandle = driver.getWindowHandle();
            WebDriver secondDriver = driver.switchTo().window(currentHandle);
            spiderData(dataList, secondDriver, tableXpath, actionXPath, numCount++);
        }
//        List<WebElement> aList = next.findElements(By.xpath("a"));
//        for (WebElement web : aList) {
//            String nextText = web.getText();
//            log.error(nextText);

//
//        }
    }


}
