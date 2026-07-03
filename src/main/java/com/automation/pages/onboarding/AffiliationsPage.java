package com.automation.pages.onboarding;

import com.automation.base.BasePage;
import com.automation.utils.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AffiliationsPage extends BasePage {
    private static final Logger logger = LogManager.getLogger(AffiliationsPage.class);

    // Locators
    private final By affiliateEmployerInput = By.id("Employer0");
    private final By affiliateStartDateInput = By.id("employStartDate0");
    private final By affiliateStateDropdown = By.xpath("//angular2-multiselect[@name='updateStateName']");
    private final By affiliateStateOptions = By.xpath("//angular2-multiselect[@name='updateStateName']//li");
    private final By affiliateCityDropdown = By.xpath("//angular2-multiselect[@name='cityNameSelect']");
    private final By affiliateCityOptions = By.xpath("//angular2-multiselect[@name='cityNameSelect']//li");
    private final By affiliateEndDateInput = By.id("employEndDate0");
    private final By affiliationsSaveAndNextButton = By.xpath("//button[@type='submit' and contains(., 'Save & Next')]");

    public AffiliationsPage(WebDriver driver) {
        super(driver);
    }

    public void fillAffiliationsInformation(
            String employer, String startDate, String state, String city, String endDate) {
        
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));
        
        logger.info("Filling Affiliations & Work History: Employer='{}', StartDate='{}', State='{}', City='{}', EndDate='{}'", employer, startDate, state, city, endDate);
        elementUtils.sendKeys(affiliateEmployerInput, employer, timeout);
        elementUtils.sendKeys(affiliateStartDateInput, startDate, timeout);
        
        logger.info("Selecting State Name: {}", state);
        elementUtils.selectFromCustomDropdown(affiliateStateDropdown, affiliateStateOptions, state, timeout);
        
        try { Thread.sleep(1500); } catch(Exception e) {} // Wait for cities to load dynamically
        
        logger.info("Selecting City: {}", city);
        elementUtils.selectFromCustomDropdown(affiliateCityDropdown, affiliateCityOptions, city, timeout);
        
        elementUtils.sendKeys(affiliateEndDateInput, endDate, timeout);
    }

    public void clickAffiliationsSaveAndNext() {
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));
        logger.info("Clicking Affiliations Save & Next button...");
        elementUtils.click(affiliationsSaveAndNextButton, timeout);
    }
}
