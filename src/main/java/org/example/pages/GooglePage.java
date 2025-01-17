package org.example.pages;

import org.example.configs.PropertiesManager;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.example.configs.Constants.BASE_URL_PROPERTY;

public class GooglePage {
    private WebDriver driver;

    private By searchField = new By.ByXPath("(//textarea)[1]");

    public GooglePage(WebDriver driver) {
        this.driver = driver;
    }

    public void openSearchPage() {
        driver.get(PropertiesManager.getProperty(BASE_URL_PROPERTY));
    }

    public void searchForText(String text) {
        WebElement element = driver.findElement(searchField);
        element.clear();
        element.sendKeys(text);
        element.sendKeys(Keys.ENTER);
    }
}
