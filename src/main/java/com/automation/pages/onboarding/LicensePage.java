package com.automation.pages.onboarding;

import com.automation.base.BasePage;
import com.automation.utils.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.time.Duration;
import java.util.List;

public class LicensePage extends BasePage {
    private static final Logger logger = LogManager.getLogger(LicensePage.class);

    private final By licenseSaveAndNextButton = By.xpath("//button[@type='submit' and contains(., 'Save & Next')] | //button[contains(., 'Save & Next')]");

    public LicensePage(WebDriver driver) {
        super(driver);
    }

    public void fillIncompleteLicenses() {
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));
        
        // Wait for the loader to disappear
        try {
            logger.info("Waiting for state license loader to disappear...");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(25));
            wait.until(org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOfElementLocated(By.tagName("app-circular-loader")));
            logger.info("State license loader disappeared.");
            Thread.sleep(1500);
        } catch (Exception e) {
            logger.warn("Finished waiting for loader: {}", e.getMessage());
        }
        
        logger.info("Checking for incomplete licenses on License/DEA page...");
        
        // 1. Fill empty License States (when rendered directly as dropdowns)
        try {
            List<WebElement> stateDropdowns = driver.findElements(By.xpath("//angular2-multiselect[@name='updateStateCode']"));
            for (int i = 0; i < stateDropdowns.size(); i++) {
                WebElement dropdown = stateDropdowns.get(i);
                String currentText = dropdown.getText().trim();
                if (currentText.contains("Select State Code") || currentText.isEmpty()) {
                    logger.info("License State dropdown {} is empty. Selecting CA...", i + 1);
                    ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", dropdown);
                    Thread.sleep(1000);
                    
                    WebElement option = driver.findElement(By.xpath("(//angular2-multiselect[@name='updateStateCode'])[" + (i + 1) + "]//li[normalize-space(.)='CA']"));
                    ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", option);
                    Thread.sleep(1000);
                }
            }
        } catch (Exception e) {
            logger.warn("Error handling license state dropdowns: {}", e.getMessage());
        }

        // 2. Fill empty License States (when rendered as disabled input with edit button)
        try {
            List<WebElement> stateNames = driver.findElements(By.xpath("//input[@placeholder='Edit State Name' or contains(@placeholder, 'State')]"));
            for (int i = 0; i < stateNames.size(); i++) {
                WebElement input = stateNames.get(i);
                String val = input.getAttribute("value");
                if (val == null || val.trim().isEmpty()) {
                    logger.info("License State input {} is empty. Attempting to edit...", i + 1);
                    try {
                        WebElement editBtn = driver.findElement(By.id("licenseStateNameEditButton" + i));
                        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", editBtn);
                        Thread.sleep(500);
                    } catch (Exception ex) {
                        logger.warn("Could not click licenseStateNameEditButton{}: {}", i, ex.getMessage());
                    }
                    
                    try {
                        if (input.isEnabled()) {
                            elementUtils.sendKeys(By.xpath("(//input[@placeholder='Edit State Name' or contains(@placeholder, 'State')])[" + (i + 1) + "]"), "California", timeout);
                        } else {
                            logger.info("Input still disabled. Setting value via JS...");
                            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].value = 'California';", input);
                            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].dispatchEvent(new Event('input', { bubbles: true }));", input);
                            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", input);
                        }
                    } catch (Exception ex) {
                        logger.warn("Could not set License State value: {}", ex.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Error handling license states: {}", e.getMessage());
        }

        // 3. Fill empty License Numbers
        try {
            List<WebElement> licenseNumbers = driver.findElements(By.xpath("//input[@placeholder='Enter License Number' or contains(@placeholder, 'License Number') or contains(@placeholder, 'DEA Number')]"));
            for (int i = 0; i < licenseNumbers.size(); i++) {
                WebElement input = licenseNumbers.get(i);
                String val = input.getAttribute("value");
                if (val == null || val.trim().isEmpty()) {
                    logger.info("License/DEA Number input {} is empty. Filling it...", i + 1);
                    elementUtils.sendKeys(By.xpath("(//input[@placeholder='Enter License Number' or contains(@placeholder, 'License Number') or contains(@placeholder, 'DEA Number')])[" + (i + 1) + "]"), "99999" + i, timeout);
                }
            }
        } catch (Exception e) {
            logger.warn("Error handling license numbers: {}", e.getMessage());
        }

        // 4. Fill empty License Types (when rendered directly as dropdowns)
        try {
            List<WebElement> typeDropdowns = driver.findElements(By.xpath("//angular2-multiselect[contains(@id, 'licenseTypeSelectBox')]"));
            for (int i = 0; i < typeDropdowns.size(); i++) {
                WebElement dropdown = typeDropdowns.get(i);
                String currentText = dropdown.getText().trim();
                if (currentText.contains("Select License Type") || currentText.isEmpty()) {
                    logger.info("License Type dropdown {} is empty. Selecting MD...", i + 1);
                    ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", dropdown);
                    Thread.sleep(1000);
                    
                    WebElement option = driver.findElement(By.xpath("//angular2-multiselect[@id='licenseTypeSelectBox" + i + "']//li[normalize-space(.)='MD']"));
                    ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", option);
                    Thread.sleep(1000);
                }
            }
        } catch (Exception e) {
            logger.warn("Error handling license type dropdowns: {}", e.getMessage());
        }

        // 5. Fill empty License Types (when rendered as disabled input with edit button)
        try {
            List<WebElement> licenseTypes = driver.findElements(By.xpath("//input[@placeholder='Edit License Type' or @placeholder='Enter License Type' or contains(@placeholder, 'License Type')]"));
            for (int i = 0; i < licenseTypes.size(); i++) {
                WebElement input = licenseTypes.get(i);
                String val = input.getAttribute("value");
                if (val == null || val.trim().isEmpty()) {
                    logger.info("License Type input {} is empty. Attempting to edit...", i + 1);
                    try {
                        WebElement editBtn = driver.findElement(By.id("licenseTypeEditButton" + i));
                        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", editBtn);
                        Thread.sleep(1000);
                    } catch (Exception ex) {
                        logger.warn("Could not click licenseTypeEditButton{}: {}", i, ex.getMessage());
                    }

                    try {
                        WebElement selectBox = driver.findElement(By.id("licenseTypeSelectBox" + i));
                        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", selectBox);
                        Thread.sleep(1000);
                        
                        WebElement option = driver.findElement(By.xpath("//angular2-multiselect[@id='licenseTypeSelectBox" + i + "']//li[normalize-space(.)='MD']"));
                        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", option);
                        Thread.sleep(1000);
                        logger.info("Successfully selected MD option in dropdown {}", i + 1);
                    } catch (Exception ex) {
                        logger.warn("Could not select MD via dropdown interaction: {}. Attempting fallback...", ex.getMessage());
                        try {
                            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].value = 'MD';", input);
                            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].dispatchEvent(new Event('input', { bubbles: true }));", input);
                            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", input);
                        } catch (Exception jsEx) {
                            logger.warn("JS fallback also failed: {}", jsEx.getMessage());
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Error handling license types: {}", e.getMessage());
        }

        // 6. Fill empty Expiration Dates
        try {
            List<WebElement> expirationDates = driver.findElements(By.xpath("//input[@type='date']"));
            for (int i = 0; i < expirationDates.size(); i++) {
                WebElement input = expirationDates.get(i);
                String val = input.getAttribute("value");
                if (val == null || val.trim().isEmpty()) {
                    logger.info("Expiration Date input {} is empty. Filling it...", i + 1);
                    elementUtils.sendKeys(By.xpath("(//input[@type='date'])[" + (i + 1) + "]"), "12312027", timeout);
                }
            }
        } catch (Exception e) {
            logger.warn("Error handling license expiration dates: {}", e.getMessage());
        }
    }

    public void clickLicenseSaveAndNext() {
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));
        logger.info("Clicking License Save & Next button...");
        elementUtils.click(licenseSaveAndNextButton, timeout);
    }
}
