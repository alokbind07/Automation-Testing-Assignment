package com.automation.pages.onboarding;

import com.automation.base.BasePage;
import com.automation.utils.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProfessionalReferencePage extends BasePage {
    private static final Logger logger = LogManager.getLogger(ProfessionalReferencePage.class);

    // Locators
    private final By uploadReferenceLabel = By.xpath("//label[@for='boardCertified12']");
    private final By referenceSaveAndNextButton = By.xpath("//button[@type='submit' and contains(., 'Save & Next')]");

    public ProfessionalReferencePage(WebDriver driver) {
        super(driver);
    }

    public void selectEnterReferenceDetails() {
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));
        logger.info("Selecting 'Enter Professional Reference Details' radio button...");
        By radio = By.xpath("//label[@for='boardCertified11']");
        elementUtils.click(radio, timeout);
    }

    public void fillProfessionalReferenceDetails(
            String name1, String title1, String phone1, String email1,
            String name2, String title2, String phone2, String email2) {
        
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));
        
        // ------------------ REFERENCE 1 ------------------
        logger.info("Filling Reference 1 Details: Name='{}', Title='{}', Phone='{}', Email='{}'", name1, title1, phone1, email1);
        
        elementUtils.sendKeys(By.id("referenceName0"), name1, timeout);
        elementUtils.sendKeys(By.id("Title0"), title1, timeout);
        
        try {
            logger.info("Opening Specialty dropdown for Reference 1...");
            elementUtils.click(By.id("providerSpecialtySelectBox0"), timeout);
            Thread.sleep(1000);
            logger.info("Selecting first specialty option for Reference 1...");
            WebElement specOption = elementUtils.waitForElementToBeVisible(By.xpath("//angular2-multiselect[@id='providerSpecialtySelectBox0']//li[1]"), timeout);
            specOption.click();
            Thread.sleep(500);
        } catch (Exception e) {
            logger.warn("Failed to select Specialty dropdown option for Reference 1: {}", e.getMessage());
        }
        
        try {
            logger.info("Opening Relation dropdown for Reference 1...");
            By relationSelect = By.xpath("(//nz-select[contains(@nzplaceholder, 'Relation') or @nzplaceholder='Select Relation'])[1]");
            elementUtils.click(relationSelect, timeout);
            Thread.sleep(1000);
            logger.info("Selecting 'Colleague' option for Reference 1...");
            By colleagueOption = By.xpath("(//nz-option-item[contains(., 'Colleague')] | //*[contains(@class, 'ant-select-item-option-content') and contains(., 'Colleague')] | //li[contains(text(), 'Colleague')] | //*[contains(text(), 'Colleague')])[last()]");
            elementUtils.click(colleagueOption, timeout);
            Thread.sleep(500);
        } catch (Exception e) {
            logger.warn("Failed to select Relation option for Reference 1: {}", e.getMessage());
        }
        
        elementUtils.sendKeys(By.id("phone0"), phone1, timeout);
        elementUtils.sendKeys(By.id("email0"), email1, timeout);
        
        try {
            logger.info("Marking Reference 1 Mailing Address as not required...");
            WebElement checkbox = elementUtils.waitForElementToBeVisible(By.id("mailingAddress0"), timeout);
            if (!checkbox.isSelected()) {
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", checkbox);
            }
            Thread.sleep(500);
        } catch (Exception e) {
            logger.warn("Failed to check mailingAddress0 checkbox: {}", e.getMessage());
        }

        // Scroll down to bring Reference 2 into view
        try {
            logger.info("Scrolling down to Reference 2...");
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("window.scrollBy(0, 500);");
            Thread.sleep(1000);
        } catch (Exception e) {
            logger.warn("Scroll failed: {}", e.getMessage());
        }

        // ------------------ REFERENCE 2 ------------------
        logger.info("Filling Reference 2 Details: Name='{}', Title='{}', Phone='{}', Email='{}'", name2, title2, phone2, email2);
        
        elementUtils.sendKeys(By.id("referenceName1"), name2, timeout);
        elementUtils.sendKeys(By.id("Title1"), title2, timeout);
        
        try {
            logger.info("Opening Specialty dropdown for Reference 2...");
            elementUtils.click(By.id("providerSpecialtySelectBox1"), timeout);
            Thread.sleep(1000);
            logger.info("Selecting first specialty option for Reference 2...");
            WebElement specOption = elementUtils.waitForElementToBeVisible(By.xpath("//angular2-multiselect[@id='providerSpecialtySelectBox1']//li[1]"), timeout);
            specOption.click();
            Thread.sleep(500);
        } catch (Exception e) {
            logger.warn("Failed to select Specialty dropdown option for Reference 2: {}", e.getMessage());
        }
        
        try {
            logger.info("Opening Relation dropdown for Reference 2...");
            By relationSelect = By.xpath("(//nz-select[contains(@nzplaceholder, 'Relation') or @nzplaceholder='Select Relation'])[2]");
            elementUtils.click(relationSelect, timeout);
            Thread.sleep(1000);
            logger.info("Selecting 'Colleague' option for Reference 2...");
            By colleagueOption = By.xpath("(//nz-option-item[contains(., 'Colleague')] | //*[contains(@class, 'ant-select-item-option-content') and contains(., 'Colleague')] | //li[contains(text(), 'Colleague')] | //*[contains(text(), 'Colleague')])[last()]");
            elementUtils.click(colleagueOption, timeout);
            Thread.sleep(500);
        } catch (Exception e) {
            logger.warn("Failed to select Relation option for Reference 2: {}", e.getMessage());
        }
        
        elementUtils.sendKeys(By.id("phone1"), phone2, timeout);
        elementUtils.sendKeys(By.id("email1"), email2, timeout);
        
        try {
            logger.info("Marking Reference 2 Mailing Address as not required...");
            WebElement checkbox = elementUtils.waitForElementToBeVisible(By.id("mailingAddress1"), timeout);
            if (!checkbox.isSelected()) {
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", checkbox);
            }
            Thread.sleep(500);
        } catch (Exception e) {
            logger.warn("Failed to check mailingAddress1 checkbox: {}", e.getMessage());
        }
    }

    public void selectUploadReferenceApprovalLetter() {
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));
        logger.info("Selecting 'Upload Reference Approval Letter' radio button...");
        elementUtils.click(uploadReferenceLabel, timeout);
    }

    public void uploadReferenceLetters(String filePath) {
        logger.info("Uploading reference letters using programmatic DataTransfer injection...");

        // Upload Reference Letter 1
        try {
            logger.info("Injecting Reference Letter 1...");
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "const fileEl = document.getElementById('file');" +
                "const origClick = fileEl.click;" +
                "fileEl.click = function() {};" +
                "document.getElementById('upload0').click();" +
                "const myFile = new File(['Dummy PDF Content 1'], 'reference_test.pdf', { type: 'application/pdf' });" +
                "const dt = new DataTransfer();" +
                "dt.items.add(myFile);" +
                "fileEl.files = dt.files;" +
                "fileEl.dispatchEvent(new Event('change', { bubbles: true }));" +
                "fileEl.click = origClick;"
            );
            Thread.sleep(4000);
        } catch (Exception e) {
            logger.error("Failed to upload Reference Letter 1: {}", e.getMessage());
        }

        // Upload Reference Letter 2
        try {
            logger.info("Injecting Reference Letter 2...");
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "const fileEl = document.getElementById('file');" +
                "const origClick = fileEl.click;" +
                "fileEl.click = function() {};" +
                "document.getElementById('upload1').click();" +
                "const myFile = new File(['Dummy PDF Content 2'], 'reference_test.pdf', { type: 'application/pdf' });" +
                "const dt = new DataTransfer();" +
                "dt.items.add(myFile);" +
                "fileEl.files = dt.files;" +
                "fileEl.dispatchEvent(new Event('change', { bubbles: true }));" +
                "fileEl.click = origClick;"
            );
            Thread.sleep(4000);
        } catch (Exception e) {
            logger.error("Failed to upload Reference Letter 2: {}", e.getMessage());
        }
    }

    public void clickReferenceSaveAndNext() {
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));
        logger.info("Clicking Professional Reference Save & Next button...");
        elementUtils.click(referenceSaveAndNextButton, timeout);
    }
}
