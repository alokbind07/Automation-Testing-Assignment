package com.automation.pages.onboarding;

import com.automation.base.BasePage;
import com.automation.utils.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.List;

public class PersonalInformationPage extends BasePage {
    private static final Logger logger = LogManager.getLogger(PersonalInformationPage.class);

    // Locators
    private final By ssnInput = By
            .xpath("//input[contains(@placeholder, 'SSN') or @name='ssn' or contains(@id, 'ssn')]");
    private final By emailInput = By.xpath(
            "//input[contains(@placeholder, 'Email Address') or @name='email' or @placeholder='Enter Email Address']");
    private final By dobInput = By.xpath(
            "//input[contains(@placeholder, 'MM') or contains(@placeholder, 'DD') or @placeholder='MM / DD / YYYY' or @type='date']");
    private final By phoneInput = By
            .xpath("//input[@name='phone' or contains(@placeholder, 'Phone Number')]");
    private final By genderDropdown = By.id("genderSelectBox");
    private final By genderOptions = By.xpath("//angular2-multiselect[@id='genderSelectBox']//li");
    private final By saveAndNextButton = By
            .xpath("//button[contains(., 'Save & Next') or contains(text(), 'Save & Next')]");

    public PersonalInformationPage(WebDriver driver) {
        super(driver);
    }

    public boolean isFormPageLoaded() {
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));
        return elementUtils.isDisplayed(ssnInput, timeout);
    }

    public void fillBasicInformation(String ssn, String email, String dob, String phone, String gender,
            String boardName) {
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));

        logger.info("Filling Basic Information form: SSN='{}', Email='{}', DOB='{}', Phone='{}', Gender='{}'", ssn,
                email, dob, phone, gender);
        elementUtils.sendKeys(ssnInput, ssn, timeout);
        elementUtils.sendKeys(emailInput, email, timeout);
        elementUtils.sendKeys(dobInput, dob, timeout);
        elementUtils.sendKeys(phoneInput, phone, timeout);

        logger.info("Selecting Gender: {}", gender);
        elementUtils.selectFromCustomDropdown(genderDropdown, genderOptions, gender, timeout);

        // Scroll the form/window to the bottom to ensure fields like Board Certified
        // are in the viewport
        try {
            logger.info("Scrolling page to the bottom...");
            ((org.openqa.selenium.JavascriptExecutor) driver)
                    .executeScript("window.scrollTo(0, document.body.scrollHeight);");
            Thread.sleep(1000); // Wait for rendering after scroll
        } catch (Exception e) {
            logger.warn("Scroll to bottom failed: {}", e.getMessage());
        }

        // Find all Yes radio labels on the page
        By yesLabelLocator = By.xpath("//label[contains(@for, 'boardCertified11')]");
        List<WebElement> yesLabels = driver.findElements(yesLabelLocator);
        logger.info("Found {} board certified Yes radio labels", yesLabels.size());

        for (int i = 0; i < yesLabels.size(); i++) {
            WebElement yesLabel = yesLabels.get(i);
            logger.info("Selecting Board Certified: Yes for taxonomy index {}", i);
            try {
                yesLabel.click();
            } catch (Exception e) {
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", yesLabel);
            }

            try {
                Thread.sleep(1000); // Wait for dynamic certifying board dropdown to render
            } catch (Exception e) {
                // ignore
            }

            By boardDropdown = By.id("boardNameSelectBox" + i);
            By boardOptions = By.xpath("//angular2-multiselect[@id='boardNameSelectBox" + i + "']//li");
            logger.info("Selecting Certifying Board for taxonomy index {}: {}", i, boardName);
            elementUtils.selectFromCustomDropdown(boardDropdown, boardOptions, boardName, timeout);
        }

        // Handle any empty Taxonomy State dropdowns to prevent validation block on save
        try {
            By taxonomyStateLocator = By.xpath("//angular2-multiselect[@name='updateStateName']");
            List<WebElement> stateDropdowns = driver.findElements(taxonomyStateLocator);
            logger.info("Found {} taxonomy state dropdowns", stateDropdowns.size());

            for (int i = 0; i < stateDropdowns.size(); i++) {
                String currentText = stateDropdowns.get(i).getText().trim();
                logger.info("Taxonomy state index {} text: '{}'", i, currentText);
                if (currentText.toLowerCase().contains("select state") || currentText.isEmpty()) {
                    By specificDropdown = By
                            .xpath("(//angular2-multiselect[@name='updateStateName'])[" + (i + 1) + "]");
                    By specificOptions = By
                            .xpath("(//angular2-multiselect[@name='updateStateName'])[" + (i + 1) + "]//li");
                    logger.info("Selecting State 'California' for empty taxonomy state at index {}", i);
                    elementUtils.selectFromCustomDropdown(specificDropdown, specificOptions, "California", timeout);
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to check/fill empty taxonomy states: {}", e.getMessage());
        }
    }

    public void clickSaveAndNext() {
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));
        logger.info("Clicking Save & Next button...");
        elementUtils.click(saveAndNextButton, timeout);
    }
}
