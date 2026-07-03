package com.automation.pages.onboarding;

import com.automation.base.BasePage;
import com.automation.utils.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UploadDocumentsPage extends BasePage {
    private static final Logger logger = LogManager.getLogger(UploadDocumentsPage.class);

    public UploadDocumentsPage(WebDriver driver) {
        super(driver);
    }

    public void selectNotApplicableForDocumentsAndSubmit() {
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));
        logger.info("Selecting 'Not Applicable' for Document 1 (uploadNotApl0)...");
        elementUtils.click(By.id("uploadNotApl0"), timeout);
        
        try { Thread.sleep(1000); } catch (Exception e) {}
        
        logger.info("Selecting 'Not Applicable' for Document 2 (uploadNotApl1)...");
        elementUtils.click(By.id("uploadNotApl1"), timeout);
        
        try { Thread.sleep(1000); } catch (Exception e) {}
        
        logger.info("Clicking Documents Save & Next button (complete)...");
        elementUtils.click(By.id("complete"), timeout);
    }
}
