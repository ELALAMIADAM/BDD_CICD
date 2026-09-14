package com.example.hooks;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import io.cucumber.java.After;
import io.cucumber.java.Before;

public class BaseTest {
    public static WebDriver driver;

    @Before 
    public void setUp(){
        URL gridUrl = null;
        try {
            gridUrl = new URL("http://127.1.0.1:4444");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        ChromeOptions cho = new ChromeOptions();
        driver = new RemoteWebDriver(gridUrl, cho);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @After 
    public void tearDown(){
        if(driver != null){
            driver.quit();
            driver = null;
        }
    }
}
