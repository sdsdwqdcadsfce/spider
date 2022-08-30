package com.peait.spider.controller;

import com.peait.spider.entity.HkPositionList;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@EnableScheduling
@Slf4j
public class DfPageListSchedule {
    @Resource
    private HkPositionListMapper hkPositionListMapper;

//    @Scheduled(cron = "0 0 1 ? * 6 ")
    public void scheduler() throws InterruptedException {
        //获取文件路径 请求的文件地址 爬取的表格小path 爬取的数据xpath
        log.info("开始进入");
        HashMap<String, String> dataMap = hkPositionListMapper.selectAddressDataById("hk_position_list");
        String queryUrl = dataMap.get("queryUrl");
        LocalDate now = LocalDate.now();
        LocalDate yestoday = now.minusDays(1);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String yesdayStr = yestoday.format(dateTimeFormatter);
        queryUrl = queryUrl.replace("{date}", yesdayStr);
        System.getProperties().setProperty("webdriver.chrome.driver", dataMap.get("chromeUrl"));
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("-headless");
        chromeOptions.addArguments("no-sandbox");
        WebDriver webDriver = new ChromeDriver(chromeOptions);
        webDriver.get(queryUrl);
        List<HkPositionList> dataList = new ArrayList<>();
        String tableXpath = dataMap.get("tableXpath");
        String actionXPath = dataMap.get("actionXPath");
        log.error(" //开始循环查询");
        int i=0;
        spiderData(dataList, webDriver, tableXpath, actionXPath,i);
        log.error("程序退出");
        webDriver.close();
        webDriver.quit();

    }

    private void spiderData(List<HkPositionList> dataList, WebDriver driver, String tableXpath, String actionXPath,int numCount) throws InterruptedException {
      if(numCount<=45){
          Thread.sleep(10000);
          WebElement tableElement = driver.findElement(By.xpath(tableXpath));
          List<WebElement> tr = tableElement.findElements(By.xpath("tr"));
          List<String> codeList = hkPositionListMapper.selectCode();
          for (int i = 0; i < tr.size(); i++) {
              List<WebElement> td = tr.get(i).findElements(By.xpath("td"));
              HkPositionList hkPositionList = new HkPositionList();
              hkPositionList.setDataCode(td.get(1).getText());
              hkPositionList.setDataName(td.get(2).getText());
              hkPositionList.setDataTime(td.get(0).getText());
              hkPositionList.setDataUrl("http://data.eastmoney.com"+td.get(5).findElements(By.xpath("a")).get(1).getAttribute("href"));
              dataList.add(hkPositionList);
              //新增数据
              log.error("新增" + td.get(2).getText());
              List<HkPositionList> collect = dataList.stream().filter(x -> !codeList.contains(x.getDataCode())).collect(Collectors.toList());
              if (collect.size() != 0) {
                  for (HkPositionList hk : collect) {
                      if(hk.getDataName().contains("st")|| hk.getDataName().contains("ST") || hk.getDataName().contains("退")){
                          continue;
                      }
                      hkPositionListMapper.insertSelective(hk);
                  }
              }
              dataList.clear();
          }
          //点击下一页链接
          WebElement next = ((ChromeDriver) driver).findElementByXPath(actionXPath);
          List<WebElement> aList = next.findElements(By.xpath("a"));
          for (WebElement web:aList) {
              String nextText = web.getText();
              log.error(nextText);
              if (nextText.equals("下一页")) {
                  Actions actions = new Actions(driver);
                  actions.moveToElement(web).click().perform();
                  //点击完下一页 等待10秒
                  Thread.sleep(10000);
                  String currentHandle = driver.getWindowHandle();
                  WebDriver secondDriver = driver.switchTo().window(currentHandle);
                  spiderData(dataList, secondDriver, tableXpath, actionXPath,numCount++);
              }
          }

      }
    }
}
