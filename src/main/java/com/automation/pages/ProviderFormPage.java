package com.automation.pages;

import com.automation.base.BasePage;
import com.automation.utils.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.time.Duration;
import java.util.Set;

public class ProviderFormPage extends BasePage {
    private static final Logger logger = LogManager.getLogger(ProviderFormPage.class);

    // Locator to check if form page is loaded
    private final By ssnInput = By.xpath("//input[contains(@placeholder, 'SSN') or @name='ssn' or contains(@id, 'ssn')]");

    public ProviderFormPage(WebDriver driver) {
        super(driver);
    }

    public void switchToNewTab(String originalWindowHandle) {
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));

        logger.info("Waiting for new tab to open...");
        wait.until(d -> d.getWindowHandles().size() > 1);

        Set<String> allWindows = driver.getWindowHandles();
        for (String windowHandle : allWindows) {
            if (!windowHandle.equals(originalWindowHandle)) {
                driver.switchTo().window(windowHandle);
                logger.info("Switched to new tab. Title: '{}', URL: '{}'", driver.getTitle(), driver.getCurrentUrl());
                break;
            }
        }
    }

    public boolean isFormPageLoaded() {
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));
        return elementUtils.isDisplayed(ssnInput, timeout);
    }
}
