package com.automation.pages.onboarding;

import com.automation.base.BasePage;
import com.automation.utils.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.List;

public class PortalLoginsPage extends BasePage {
    private static final Logger logger = LogManager.getLogger(PortalLoginsPage.class);

    private final By loginsSaveAndNextButton = By.xpath("//button[@name='complete'] | //button[@type='submit' and contains(., 'Save & Next')]");

    public PortalLoginsPage(WebDriver driver) {
        super(driver);
    }

    public void fillPortalLogins() {
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));
        logger.info("Filling all username and password fields on Portal Logins page...");
        
        try {
            List<WebElement> userInputs = driver.findElements(By.xpath("//input[contains(@id, 'userName')] | //input[contains(@placeholder, 'username')]"));
            for (int i = 0; i < userInputs.size(); i++) {
                WebElement input = userInputs.get(i);
                if (input.getAttribute("value") == null || input.getAttribute("value").trim().isEmpty()) {
                    logger.info("Filling username input {}...", i + 1);
                    elementUtils.sendKeys(By.xpath("(//input[contains(@id, 'userName') or contains(@placeholder, 'username')])[" + (i + 1) + "]"), "autoUser" + (i + 1), timeout);
                }
            }
        } catch (Exception e) {
            logger.warn("Error filling usernames: {}", e.getMessage());
        }

        try {
            List<WebElement> passInputs = driver.findElements(By.xpath("//input[contains(@id, 'password')] | //input[contains(@placeholder, 'password')]"));
            for (int i = 0; i < passInputs.size(); i++) {
                WebElement input = passInputs.get(i);
                if (input.getAttribute("value") == null || input.getAttribute("value").trim().isEmpty()) {
                    logger.info("Filling password input {}...", i + 1);
                    elementUtils.sendKeys(By.xpath("(//input[contains(@id, 'password') or contains(@placeholder, 'password')])[" + (i + 1) + "]"), "AutoPass123!", timeout);
                }
            }
        } catch (Exception e) {
            logger.warn("Error filling passwords: {}", e.getMessage());
        }
    }

    public void clickLoginsSaveAndNext() {
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));
        logger.info("Clicking Portal Logins Save & Next button...");
        elementUtils.click(loginsSaveAndNextButton, timeout);
    }
}
