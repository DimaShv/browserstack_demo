package config;

import org.example.configs.WebDriverFactory;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;

import static org.example.configs.Constants.DRIVER_ATTRIBUTE;

public class DriverListener implements ITestListener {

    @Override
    public void onStart(ITestContext context) {
        WebDriver driver = WebDriverFactory.getDriver();
        context.setAttribute(DRIVER_ATTRIBUTE, driver);
    }

    @Override
    public void onFinish(ITestContext context) {
        WebDriverFactory.quitDriver();
    }
}
