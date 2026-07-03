package com.automation.pages.onboarding;

import com.automation.utils.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DeaPage extends LicensePage {
    private static final Logger logger = LogManager.getLogger(DeaPage.class);

    private final By deaSaveAndNextButton = By.xpath("//button[@name='saveBut'] | //button[@type='submit' and contains(., 'Save & Next')]");

    public DeaPage(WebDriver driver) {
        super(driver);
    }

    public void clickDeaSaveAndNext() {
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));
        logger.info("Clicking DEA Save & Next button...");
        elementUtils.click(deaSaveAndNextButton, timeout);
    }
}
