package com.automation.pages.onboarding;

import com.automation.base.BasePage;
import com.automation.utils.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PrivilegesPage extends BasePage {
    private static final Logger logger = LogManager.getLogger(PrivilegesPage.class);

    // Locators
    private final By corePrivilegesCheckbox = By.xpath("//input[@id='flexCheckDefault'] | //input[@type='checkbox' and following-sibling::label[contains(text(), 'CORE PRIVILEGES') or contains(text(), 'Core Privileges')]]");
    private final By privilegesSaveAndNextButton = By.xpath("//button[@type='submit' and contains(., 'Save & Next')]");

    public PrivilegesPage(WebDriver driver) {
        super(driver);
    }

    public void selectCorePrivileges() {
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));
        logger.info("Selecting Core Privileges checkbox...");
        WebElement checkbox = elementUtils.waitForElementToBeVisible(corePrivilegesCheckbox, timeout);
        if (!checkbox.isSelected()) {
            elementUtils.click(corePrivilegesCheckbox, timeout);
        }
    }

    public void clickPrivilegesSaveAndNext() {
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));
        logger.info("Clicking Privileges Save & Next button...");
        elementUtils.click(privilegesSaveAndNextButton, timeout);
    }
}
