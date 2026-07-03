package com.automation.pages.onboarding;

import com.automation.base.BasePage;
import com.automation.utils.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EducationPage extends BasePage {
    private static final Logger logger = LogManager.getLogger(EducationPage.class);

    // Locators
    // 1. Professional School
    private final By profSchoolDropdown = By.id("profSchoolName0");
    private final By profSchoolOptions = By.xpath("//angular2-multiselect[@id='profSchoolName0']//li");
    private final By profDegreeInput = By.id("schoolDegree0");
    private final By profAreaInput = By.id("schoolAreaTraining0");
    private final By profStartDateInput = By.id("schoolStartDate0");
    private final By profEndDateInput = By.id("schoolEndDate0");
    private final By profStateDropdown = By.xpath("(//div[@id='collapseCredOne0']//angular2-multiselect[@name='updateStateName'])[1]");
    private final By profStateOptions = By.xpath("(//div[@id='collapseCredOne0']//angular2-multiselect[@name='updateStateName'])[1]//li");

    // 2. Undergraduate Education
    private final By ugSchoolDropdown = By.id("ugSchoolName0");
    private final By ugSchoolOptions = By.xpath("//angular2-multiselect[@id='ugSchoolName0']//li");
    private final By ugDegreeInput = By.xpath("(//div[@id='collapseCredOne1']//input[contains(@placeholder, 'Degree')])[1]");
    private final By ugAreaInput = By.xpath("(//div[@id='collapseCredOne1']//input[contains(@placeholder, 'Area of Training')])[1]");
    private final By ugStartDateInput = By.xpath("(//div[@id='collapseCredOne1']//input[contains(@placeholder, 'MM/DD/YYYY') or @type='date'])[1]");
    private final By ugEndDateInput = By.xpath("(//div[@id='collapseCredOne1']//input[contains(@placeholder, 'MM/DD/YYYY') or @type='date'])[2]");
    private final By ugStateDropdown = By.xpath("(//div[@id='collapseCredOne1']//angular2-multiselect[@name='updateStateName'])[1]");
    private final By ugStateOptions = By.xpath("(//div[@id='collapseCredOne1']//angular2-multiselect[@name='updateStateName'])[1]//li");

    // 3. Professional Training
    private final By trainingTypeDropdown = By.id("licenseTypeSelectBox0");
    private final By trainingTypeOptions = By.xpath("//angular2-multiselect[@id='licenseTypeSelectBox0']//li");
    private final By trainingInstitutionInput = By.xpath("(//div[@id='collapseCredOne2']//input[contains(@placeholder, 'Institution/Hospital Name')])[1]");
    private final By trainingDeptInput = By.xpath("(//div[@id='collapseCredOne2']//input[contains(@placeholder, 'Department')])[1]");
    private final By trainingStartDateInput = By.xpath("(//div[@id='collapseCredOne2']//input[@type='date'])[1]");
    private final By trainingEndDateInput = By.xpath("(//div[@id='collapseCredOne2']//input[@type='date'])[2]");

    // 4. Save & Next button
    private final By educationSaveAndNextButton = By.xpath("//button[@type='submit' and contains(., 'Save & Next')]");

    public EducationPage(WebDriver driver) {
        super(driver);
    }

    public void fillEducationInformation(
            String profSchool, String profDegree, String profArea, String profStart, String profEnd, String profState,
            String ugSchool, String ugDegree, String ugArea, String ugStart, String ugEnd, String ugState,
            String trainingType, String trainingInst, String trainingDept, String trainingStart, String trainingEnd) {
        
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));
        
        logger.info("Filling Professional School Information...");
        elementUtils.selectFromCustomDropdown(profSchoolDropdown, profSchoolOptions, profSchool, timeout);
        elementUtils.sendKeys(profDegreeInput, profDegree, timeout);
        elementUtils.sendKeys(profAreaInput, profArea, timeout);
        elementUtils.sendKeys(profStartDateInput, profStart, timeout);
        elementUtils.sendKeys(profEndDateInput, profEnd, timeout);
        elementUtils.selectFromCustomDropdown(profStateDropdown, profStateOptions, profState, timeout);
        
        logger.info("Filling Undergraduate Education Information...");
        elementUtils.selectFromCustomDropdown(ugSchoolDropdown, ugSchoolOptions, ugSchool, timeout);
        elementUtils.sendKeys(ugDegreeInput, ugDegree, timeout);
        elementUtils.sendKeys(ugAreaInput, ugArea, timeout);
        elementUtils.sendKeys(ugStartDateInput, ugStart, timeout);
        elementUtils.sendKeys(ugEndDateInput, ugEnd, timeout);
        elementUtils.selectFromCustomDropdown(ugStateDropdown, ugStateOptions, ugState, timeout);
        
        logger.info("Filling Professional Training Information...");
        elementUtils.selectFromCustomDropdown(trainingTypeDropdown, trainingTypeOptions, trainingType, timeout);
        elementUtils.sendKeys(trainingInstitutionInput, trainingInst, timeout);
        elementUtils.sendKeys(trainingDeptInput, trainingDept, timeout);
        elementUtils.sendKeys(trainingStartDateInput, trainingStart, timeout);
        elementUtils.sendKeys(trainingEndDateInput, trainingEnd, timeout);
    }

    public void clickEducationSaveAndNext() {
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));
        logger.info("Clicking Education Save & Next button...");
        elementUtils.click(educationSaveAndNextButton, timeout);
    }
}
