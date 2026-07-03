package com.automation.pages.onboarding;

import com.automation.base.BasePage;
import com.automation.utils.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdditionalBasicInfoPage extends BasePage {
    private static final Logger logger = LogManager.getLogger(AdditionalBasicInfoPage.class);

    // Locators
    private final By street1Input = By.id("homeline1");
    private final By stateDropdown = By.xpath("//angular2-multiselect[@name='updateStateName']");
    private final By stateOptions = By.xpath("//angular2-multiselect[@name='updateStateName']//li");
    private final By cityDropdown = By.xpath("//angular2-multiselect[@name='cityNameSelect']");
    private final By cityOptions = By.xpath("//angular2-multiselect[@name='cityNameSelect']//li");
    private final By zipcodeInput = By.xpath("//input[@name='zipcode']");
    
    private final By birthCountryDropdown = By.id("providerCountry");
    private final By birthCountryOptions = By.xpath("//angular2-multiselect[@id='providerCountry']//li");
    private final By birthStateInput = By.xpath("//input[@name='birthState']");
    private final By birthCityInput = By.xpath("//input[@name='birthCity']");
    private final By citizenshipDropdown = By.id("citizenship");
    private final By citizenshipOptions = By.xpath("//angular2-multiselect[@id='citizenship']//li");
    
    private final By emergencyContactInput = By.xpath("//input[@name='emergencyContact']");
    private final By emergencyRelationshipInput = By.xpath("//input[@name='emergencyRelationship']");
    private final By emergencyPhoneInput = By.xpath("//input[@name='emergencyPhone']");
    
    private final By additionalSaveAndNextButton = By.xpath("//button[@name='saveBut'] | //button[@type='submit' and contains(@name, 'save')]");

    public AdditionalBasicInfoPage(WebDriver driver) {
        super(driver);
    }

    public void fillAdditionalBasicInformation(
            String street1, String state, String city, String zipcode,
            String birthCountry, String birthState, String birthCity, String citizenship,
            String emergencyContact, String emergencyRelationship, String emergencyPhone) {
        
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));
        
        logger.info("Filling Additional Basic Information: Street1='{}', State='{}', City='{}', Zip='{}'", street1, state, city, zipcode);
        elementUtils.sendKeys(street1Input, street1, timeout);
        
        logger.info("Selecting State: {}", state);
        elementUtils.selectFromCustomDropdown(stateDropdown, stateOptions, state, timeout);
        
        try { Thread.sleep(1500); } catch(Exception e) {} // Wait for cities to load dynamically
        
        logger.info("Selecting City: {}", city);
        elementUtils.selectFromCustomDropdown(cityDropdown, cityOptions, city, timeout);
        
        elementUtils.sendKeys(zipcodeInput, zipcode, timeout);
        
        logger.info("Selecting Birth Country: {}", birthCountry);
        elementUtils.selectFromCustomDropdown(birthCountryDropdown, birthCountryOptions, birthCountry, timeout);
        
        // Birth State might dynamically change to a dropdown if Country is USA
        By birthStateDropdown = By.xpath("//*[contains(text(), 'Birth State')]/following::angular2-multiselect[1]");
        By birthStateOptions = By.xpath("//*[contains(text(), 'Birth State')]/following::angular2-multiselect[1]//li");
        
        if (elementUtils.isDisplayed(birthStateDropdown, 3)) {
            logger.info("Birth State is a dropdown. Selecting option: {}", birthState);
            elementUtils.selectFromCustomDropdown(birthStateDropdown, birthStateOptions, birthState, timeout);
        } else {
            logger.info("Birth State is a text input. Typing: {}", birthState);
            elementUtils.sendKeys(birthStateInput, birthState, timeout);
        }
        
        // Birth City might dynamically change to a dropdown if Country is USA
        By birthCityDropdown = By.xpath("//*[contains(text(), 'Birth City')]/following::angular2-multiselect[1]");
        By birthCityOptions = By.xpath("//*[contains(text(), 'Birth City')]/following::angular2-multiselect[1]//li");
        
        if (elementUtils.isDisplayed(birthCityDropdown, 3)) {
            logger.info("Birth City is a dropdown. Selecting option: {}", birthCity);
            elementUtils.selectFromCustomDropdown(birthCityDropdown, birthCityOptions, birthCity, timeout);
        } else {
            logger.info("Birth City is a text input. Typing: {}", birthCity);
            elementUtils.sendKeys(birthCityInput, birthCity, timeout);
        }
        
        logger.info("Selecting Citizenship: {}", citizenship);
        elementUtils.selectFromCustomDropdown(citizenshipDropdown, citizenshipOptions, citizenship, timeout);
        
        elementUtils.sendKeys(emergencyContactInput, emergencyContact, timeout);
        elementUtils.sendKeys(emergencyRelationshipInput, emergencyRelationship, timeout);
        elementUtils.sendKeys(emergencyPhoneInput, emergencyPhone, timeout);
    }

    public void clickAdditionalSaveAndNext() {
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));
        logger.info("Clicking Additional Save & Next button...");
        elementUtils.click(additionalSaveAndNextButton, timeout);
    }
}
