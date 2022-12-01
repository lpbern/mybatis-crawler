package com.mybatis.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/crawler")
public class CrawlerService {
    private static final Integer BATCH_SIZE = 10;

    ExecutorService fixedPool = Executors.newFixedThreadPool(BATCH_SIZE);

    @Resource
    private CrawlerMapper crawlerMapper;

    @GetMapping("/all/{pageLinks}")
    public void crawlerWiki(@PathVariable Long pageLinks) throws IOException {
        if (!(pageLinks > 0)) {
            throw new RuntimeException("Pagination should be greater than zero");
        }
        Document doc = Jsoup.connect(new StringBuilder().append("https://www.chotot.com/mua-ban?page=").append(pageLinks).toString()).get();
        Elements newsHeadlines = doc.getElementsByClass("AdItem_adItem__gDDQT");
        for (Element blog : newsHeadlines) {
            String linkReceived = blog.attributes().get("href").startsWith("https://") ? blog.attributes().get("href") :
                    new StringBuilder().append("https://www.chotot.com/")
                            .append(blog.attributes().get("href")).toString();
            CrawlerLink crawlerLink = new CrawlerLink();
            crawlerLink.setCreatetime(Instant.now().toEpochMilli());
            crawlerLink.setPageurl(linkReceived);
            crawlerMapper.insert(crawlerLink);
        }
    }

    @GetMapping("/process")
    public void crawlerInsidePager() {
        ChromeDriver driver = setDriverWithoutImage();
        List<CrawlerLink> data = crawlerMapper.findPerFiftyThatHasNoPhoneNumber();
        CompletableFuture.runAsync(() ->
                asyncThreadPool(data, driver) ,fixedPool);
    }
    public void asyncThreadPool(List<CrawlerLink> data, WebDriver driver){
        int count = 0;
        for(CrawlerLink link :data){
            openNewTab(driver, link.getPageurl(), count, link);
            count++;
        }
    }


    public void openNewTab(WebDriver webDriver, String url, int position, CrawlerLink link) {
        ((JavascriptExecutor) webDriver).executeScript("window.open()");
        ArrayList<String> tabs = new ArrayList<>(webDriver.getWindowHandles());
        System.out.println("tabs : " + tabs.size() + " >position: " + position + " >\t" + url);
        webDriver.switchTo().window(tabs.get(position));
        webDriver.get(url);
        WebElement webElement = webDriver.findElement(By.id("call_phone_btn"));
        String phoneNumber = webElement.getAttribute("href").split(":")[1];
        link.setPhonenumber(phoneNumber);
        crawlerMapper.update(link);
    }

    private ChromeDriver setDriverWithoutImage() {
        ChromeDriver driver = null;
        ChromeOptions chromeOptions = new ChromeOptions();
        HashMap<String, Object> images = new HashMap<String, Object>();
        images.put("images", 2);
        HashMap<String, Object> prefs = new HashMap<String, Object>();
        prefs.put("profile.default_content_setting_values", images);
        chromeOptions.setExperimentalOption("prefs", prefs);
        driver = new ChromeDriver(chromeOptions);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.manage().window().maximize();
        return driver;
    }
}
