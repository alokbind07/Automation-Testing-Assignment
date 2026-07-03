package com.automation.pages.onboarding;

import com.automation.base.BasePage;
import com.automation.utils.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HospitalAffiliationsPage extends BasePage {
    private static final Logger logger = LogManager.getLogger(HospitalAffiliationsPage.class);

    // Locators
    private final By hospitalStreet1Input = By.id("street1");
    private final By hospitalStateDropdown = By.xpath("//angular2-multiselect[@name='updateStateName']");
    private final By hospitalStateOptions = By.xpath("//angular2-multiselect[@name='updateStateName']//li");
    private final By hospitalCityDropdown = By.xpath("//angular2-multiselect[@name='cityNameSelect']");
    private final By hospitalCityOptions = By.xpath("//angular2-multiselect[@name='cityNameSelect']//li");
    private final By hospitalCountryInput = By.id("country0");
    private final By hospitalNameInput = By.id("providerHName");
    private final By hospitalStartDateInput = By.id("startDate0");
    private final By hospitalSaveAndNextButton = By.xpath("//button[@type='submit' and contains(., 'Save & Next')]");

    public HospitalAffiliationsPage(WebDriver driver) {
        super(driver);
    }

    public void fillHospitalAffiliationInformation(
            String street, String state, String city, String country, String hospitalName, String startDate) {
        
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));
        
        logger.info("Filling Hospital Affiliation: Street='{}', State='{}', City='{}', Country='{}', Hospital='{}', StartDate='{}'", street, state, city, country, hospitalName, startDate);
        elementUtils.sendKeys(hospitalStreet1Input, street, timeout);
        
        logger.info("Selecting State Name: {}", state);
        elementUtils.selectFromCustomDropdown(hospitalStateDropdown, hospitalStateOptions, state, timeout);
        
        try { Thread.sleep(1500); } catch(Exception e) {} // Wait for cities to load dynamically
        
        logger.info("Selecting City: {}", city);
        elementUtils.selectFromCustomDropdown(hospitalCityDropdown, hospitalCityOptions, city, timeout);
        
        elementUtils.sendKeys(hospitalCountryInput, country, timeout);
        elementUtils.sendKeys(hospitalNameInput, hospitalName, timeout);
        elementUtils.sendKeys(hospitalStartDateInput, startDate, timeout);
    }

    public void clickHospitalSaveAndNext() {
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));
        logger.info("Clicking Hospital Affiliations Save & Next button...");
        elementUtils.click(hospitalSaveAndNextButton, timeout);
    }
}
