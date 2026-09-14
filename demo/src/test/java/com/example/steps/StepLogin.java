package com.example.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.example.hooks.BaseTest;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class StepLogin {
    WebDriver driver = BaseTest.driver; 

    @Given("go to {string}")
    public void accessLink(String s){
        driver.get(s);
    }
    
    @When("Enter username {string} and password {string}")
    public void Enter_username_and_password(String s, String s2) {
        driver.findElement(By.cssSelector("[name=\"username\"]")).sendKeys(s);
        driver.findElement(By.cssSelector("[name=\"password\"]")).sendKeys(s2);
    }

    @When("Click Login button")
    public void Click_Login_button() {
        driver.findElement(By.className("orangehrm-login-button")).click();
    }

    @Then("Verify Existance of {string} and {string}")
    public void Verify_Existance_of_and(String s, String s2) {
        List<WebElement> cadrans = driver.findElements(By.className("orangehrm-dashboard-widget-name"));
        assertEquals("My Actions",cadrans.get(1).getText());
        assertEquals("Quick Launch",cadrans.get(2).getText());
    }
}
