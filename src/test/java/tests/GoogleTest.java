package tests;

import config.TestRailCase;
import org.example.configs.Constants;
import org.example.configs.PropertiesManager;
import org.example.pages.GooglePage;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.configs.Constants.DRIVER_ATTRIBUTE;

public class GoogleTest {
    private GooglePage googlePage;
    private WebDriver driver;

    @BeforeClass
    public void prepare(ITestContext context) {
        driver = (WebDriver) context.getAttribute(DRIVER_ATTRIBUTE);
        googlePage = new GooglePage(driver);
    }

    @Test
    @TestRailCase(id = "C2")
    public void failedTest() {
        googlePage.openSearchPage();
        String searchParam = "test";
        googlePage.searchForText(searchParam);
        String currentUrl = driver.getCurrentUrl();
        assertThat(currentUrl)
                .as("Wrong url after search")
                .startsWith(PropertiesManager.getProperty(Constants.BASE_URL_PROPERTY) + "/search?q=");
    }

    @Test
    @TestRailCase(id = "C1")
    public void successTest() {
        assertThat(true).isTrue();
    }

    @Test(dependsOnMethods = "failedTest")
    @TestRailCase(id = "C3")
    public void skippTest() {
        assertThat(true).isTrue();
    }
}
