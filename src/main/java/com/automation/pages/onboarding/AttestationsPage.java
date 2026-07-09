package com.automation.pages.onboarding;

import com.automation.base.BasePage;
import com.automation.utils.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.List;

public class AttestationsPage extends BasePage {
    private static final Logger logger = LogManager.getLogger(AttestationsPage.class);

    public AttestationsPage(WebDriver driver) {
        super(driver);
    }

    public void fillAttestationsAndOpenSignature() {
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));

        // Click all "No" radio buttons dynamically
        int index = 0;
        while (true) {
            try {
                String id = "questionRadioBtn" + index + "1";
                List<WebElement> elements = driver.findElements(By.id(id));
                if (elements.isEmpty()) {
                    break;
                }
                logger.info("Clicking radio button No for question " + index);
                String labelXpath = "//label[@for='" + id + "']";
                WebElement labelElement = elementUtils.waitForElementToBeVisible(By.xpath(labelXpath), timeout);
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", labelElement);
                index++;
                Thread.sleep(200);
            } catch (Exception e) {
                logger.warn("Failed to select No for question " + index + ": " + e.getMessage());
                break;
            }
        }

        // Click "Sign Now" button
        try {
            logger.info("Clicking the first 'Sign Now' button...");
            By signNowButton = By.xpath("//button[contains(text(), 'Sign Now') and not(ancestor::docuseal-form)]");
            elementUtils.click(signNowButton, timeout);
            Thread.sleep(3000);
        } catch (Exception e) {
            logger.error("Failed to click 'Sign Now' button: {}", e.getMessage());
        }
    }

    public void signAndSaveAttestations(String signatureName) {
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));

        // 2. Click the second "Sign Now" button inside Docuseal shadow DOM via JS
        try {
            logger.info("Clicking the second 'Sign Now' button inside Docuseal shadow DOM...");
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                    "document.querySelector('docuseal-form').shadowRoot.querySelector('#expand_form_button').click();");
            Thread.sleep(2000);
        } catch (Exception e) {
            logger.error("Failed to click Docuseal #expand_form_button: {}", e.getMessage());
        }

        // 3. Click "Type" button inside Docuseal shadow DOM via JS
        try {
            logger.info("Clicking the 'Type' tab inside Docuseal shadow DOM...");
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                    "document.querySelector('docuseal-form').shadowRoot.querySelector('#type_text_button').click();");
            Thread.sleep(2000);
        } catch (Exception e) {
            logger.error("Failed to click Docuseal #type_text_button: {}", e.getMessage());
        }

        // 4. Type the signature name
        try {
            logger.info("Typing signature name: {}", signatureName);
            WebElement docusealForm = driver.findElement(By.tagName("docuseal-form"));
            org.openqa.selenium.SearchContext shadowRoot = docusealForm.getShadowRoot();
            WebElement input = shadowRoot.findElement(By.cssSelector("#signature_text_input"));
            input.clear();
            input.sendKeys(signatureName);
            Thread.sleep(1500);
        } catch (Exception e) {
            logger.error("Failed to type signature inside Docuseal: {}", e.getMessage());
        }

        // 5. Click "Sign and Complete" button inside Docuseal shadow DOM via JS
        try {
            logger.info("Clicking the 'Sign and Complete' button inside Docuseal shadow DOM...");
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                    "document.querySelector('docuseal-form').shadowRoot.querySelector('#submit_form_button').click();");
            Thread.sleep(4000);
        } catch (Exception e) {
            logger.error("Failed to click Docuseal #submit_form_button: {}", e.getMessage());
        }

        // 6. Click the page's main "Save" button
        try {
            logger.info("Clicking the page's main 'Save' button...");
            By saveButton = By.xpath("//button[normalize-space()='Save']");
            elementUtils.click(saveButton, timeout);
            Thread.sleep(3000);
        } catch (Exception e) {
            logger.error("Failed to click main Save button: {}", e.getMessage());
        }

        // 7. Click the page's main "Submit Application" button
        // try {
        // logger.info("Clicking the 'Submit Application' button...");
        // By submitButton = By.id("complete");
        // elementUtils.click(submitButton, timeout);
        // Thread.sleep(5000);
        // } catch (Exception e) {
        // logger.error("Failed to click 'Submit Application' button: {}",
        // e.getMessage());
        // }

        // 8. Click the success modal "OK" button if it appears
        try {
            logger.info("Checking for success modal...");
            By okButton = By.xpath("//button[contains(@class, 'success-btn') or normalize-space()='OK']");
            if (elementUtils.waitForElementToBeVisible(okButton, 5) != null) {
                logger.info("Clicking success modal OK button...");
                elementUtils.click(okButton, timeout);
                Thread.sleep(2000);
            }
        } catch (Exception e) {
            logger.info("No success modal or failed to click OK: " + e.getMessage());
        }
    }
}
