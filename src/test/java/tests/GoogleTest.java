package tests;

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
    public void searchTest() {
        googlePage.openSearchPage();
        String searchParam = "test";
        googlePage.searchForText(searchParam);
        String currentUrl = driver.getCurrentUrl();
        assertThat(currentUrl)
                .as("Wrong url after search")
                .startsWith(PropertiesManager.getProperty(Constants.BASE_URL_PROPERTY) + "/search?q=");
    }
}
