package com.automation.pages;

import com.automation.base.BasePage;
import com.automation.utils.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.List;

public class AddProviderModal extends BasePage {
    private static final Logger logger = LogManager.getLogger(AddProviderModal.class);

    // Locators
    private final By workflowDropdown = By.xpath(
            "//*[contains(text(), 'Select Onboarding Workflow')]/following::div[@class='selected-list'][1] | //div[@class='selected-list']");
    // Options in Onboarding Workflow list
    private final By workflowOptions = By.xpath(
            "//div[contains(@class, 'option') or contains(@class, 'menu')]//*[contains(text(), 'Onboarding')] | //*[contains(text(), 'Complete_Test_Onboarding_Workflow')]");

    private final By addProviderCard = By.xpath(
            "//div[contains(@class, 'modal')]//*[text()='Add Provider']/parent::div | //div[contains(@class, 'modal')]//*[text()='Add Provider'] | //div[contains(@class, 'modal')]//p[text()='Add Provider']");

    // Next Button
    private final By nextButton = By.xpath(
            "//button[contains(., 'Next')] | //span[contains(text(), 'Next')] | //button[contains(text(), 'Next')]");

    // Step 2: Privileges Dropdown
    private final By privilegesDropdown = By.xpath(
            "//*[contains(text(), 'Privileges') or contains(text(), 'privilege')]/following::div[contains(@class, 'selected-list')][1]");
    // Generic options list for dropdown
    private final By dropdownOptionsList = By.xpath(
            "//li[not(contains(@class, 'breadcrumb'))] | //div[contains(@class, 'menu-list')]//div[contains(@class, 'option')]");

    // Form inputs
    private final By npiInput = By
            .xpath("//input[@name='npi' or contains(@placeholder, 'NPI') or @id='npi' or contains(@name, 'npi')]");
    private final By firstNameInput = By.xpath(
            "//input[@name='firstName' or contains(@placeholder, 'First Name') or @id='firstName' or contains(@name, 'first')]");
    private final By lastNameInput = By.xpath(
            "//input[@name='lastName' or contains(@placeholder, 'Last Name') or @id='lastName' or contains(@name, 'last')]");

    // Add button to finish the onboarding creation
    private final By addButton = By.xpath(
            "//div[contains(@class, 'modal') and contains(@class, 'show')]//button[text()='ADD' or contains(text(), 'ADD') or contains(., 'ADD') or contains(text(), 'Add')]");

    // Error element for NPI validation
    private final By npiError = By.xpath(
            "//*[contains(text(), 'NPI') and (contains(text(), 'invalid') or contains(text(), 'digit') or contains(text(), '10') or contains(text(), 'valid'))] | //*[contains(text(), 'valid NPI')] | //div[contains(@class, 'error') or contains(@class, 'invalid-feedback')]");

    public AddProviderModal(WebDriver driver) {
        super(driver);
    }

    public void selectWorkflow(String workflowName) {
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));
        By specificWorkflowOption = By.xpath("//*[contains(text(), '" + workflowName + "')]");
        elementUtils.selectFromCustomDropdown(workflowDropdown, specificWorkflowOption, workflowName, timeout);

        // Toggle the workflow dropdown trigger again to close the overlay options list
        try {
            logger.info("Clicking workflow dropdown trigger again to close the overlay options list...");
            elementUtils.click(workflowDropdown, 5);
        } catch (Exception e) {
            logger.warn("Could not click workflow dropdown trigger to close overlay, ignoring: {}", e.getMessage());
        }
    }

    public void clickAddProviderCard() {
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));
        elementUtils.clickUsingJS(addProviderCard, timeout);
    }

    public void clickNext() {
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));
        elementUtils.click(nextButton, timeout);
    }

    public void selectFirstPrivilege() {
        try {
            selectPrivilegeByName("Allergy");
        } catch (Exception e) {
            logger.warn("Failed to select 'Allergy' privilege, trying lower case fallback: {}", e.getMessage());
            try {
                selectPrivilegeByName("allergy");
            } catch (Exception ex) {
                logger.warn("Failed to select 'allergy' privilege, trying first available option...");
                selectFirstAvailablePrivilege();
            }
        }
    }

    public void selectPrivilegeByName(String privilegeName) {
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));
        logger.info("Opening Privileges dropdown and selecting option containing '{}'...", privilegeName);

        By specificOptionLocator = By.xpath("//li[contains(., '" + privilegeName + "') or contains(text(), '"
                + privilegeName + "')] | //*[contains(@class, 'option') and (contains(., '" + privilegeName
                + "') or contains(text(), '" + privilegeName + "'))]");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        boolean opened = false;

        for (int i = 0; i < 3; i++) {
            try {
                logger.info("Attempt {} to click privileges dropdown...", i + 1);
                elementUtils.click(privilegesDropdown, 5);

                // Wait directly for the specific option to be visible
                wait.until(ExpectedConditions.visibilityOfElementLocated(specificOptionLocator));
                opened = true;
                logger.info("Privileges dropdown opened and target option is visible.");
                break;
            } catch (Exception e) {
                logger.warn("Target option not visible after click attempt {}. Retrying click...", i + 1);
            }
        }

        if (!opened) {
            logger.warn(
                    "Privileges options were not visible after retries. Doing one final click and option search...");
            elementUtils.click(privilegesDropdown, timeout);
        }

        WebDriverWait optionWait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        WebElement option = optionWait.until(ExpectedConditions.visibilityOfElementLocated(specificOptionLocator));

        logger.info("Selecting option containing '{}': '{}'", privilegeName, option.getText());
        elementUtils.clickElement(option);

        // Toggle the privileges dropdown trigger again to close the overlay options
        // list
        try {
            logger.info("Clicking privileges dropdown trigger again to close the overlay options list...");
            elementUtils.click(privilegesDropdown, 5);
            // Wait for the dropdown options to be invisible/hidden
            WebDriverWait waitClose = new WebDriverWait(driver, Duration.ofSeconds(5));
            waitClose.until(ExpectedConditions.invisibilityOfElementLocated(dropdownOptionsList));
            logger.info("Privileges dropdown overlay closed successfully.");
        } catch (Exception e) {
            logger.warn("Could not click privileges dropdown trigger to close overlay, or overlay did not hide: {}",
                    e.getMessage());
        }
    }

    private void selectFirstAvailablePrivilege() {
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));
        WebDriverWait fullWait = new WebDriverWait(driver, Duration.ofSeconds(timeout));

        WebElement targetOption = null;
        for (int retry = 0; retry < 3; retry++) {
            try {
                List<WebElement> options = fullWait
                        .until(ExpectedConditions.presenceOfAllElementsLocatedBy(dropdownOptionsList));
                for (WebElement option : options) {
                    if (option.isDisplayed()) {
                        targetOption = option;
                        break;
                    }
                }
                break;
            } catch (org.openqa.selenium.StaleElementReferenceException e) {
                logger.warn("Stale element reference during fallback search. Retrying search...");
            }
        }

        if (targetOption == null) {
            logger.error("No visible privileges found in the dropdown list");
            throw new RuntimeException("No visible privileges found in the dropdown list");
        }

        logger.info("Selecting first available option from privileges dropdown: '{}'", targetOption.getText());
        elementUtils.clickElement(targetOption);

        try {
            logger.info("Clicking privileges dropdown trigger again to close the overlay options list...");
            elementUtils.click(privilegesDropdown, 5);
            WebDriverWait waitClose = new WebDriverWait(driver, Duration.ofSeconds(5));
            waitClose.until(ExpectedConditions.invisibilityOfElementLocated(dropdownOptionsList));
        } catch (Exception e) {
            logger.warn("Could not click privileges dropdown trigger to close overlay, or overlay did not hide: {}",
                    e.getMessage());
        }
    }

    public void fillDetails(String npi, String firstName, String lastName) {
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));

        // Clear name inputs first so previous attempt values are not falsely detected
        // as NPPES auto-filled
        try {
            getVisibleElement(firstNameInput, 2).clear();
            getVisibleElement(lastNameInput, 2).clear();
        } catch (Exception e) {
            // ignore
        }

        logger.info("Typing NPI: {}", npi);
        sendKeysToVisible(npiInput, npi, timeout);

        // Wait up to 5 seconds for NPPES auto-population to fill the first name field
        logger.info("Waiting for NPPES auto-fill lookup to populate names...");
        boolean autoFilled = false;
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < 4000) {
            String populatedFirst = getAttributeOfVisible(firstNameInput, "value", 2);
            if (populatedFirst != null && !populatedFirst.trim().isEmpty()) {
                autoFilled = true;
                logger.info("NPPES auto-fill lookup detected. First Name: '{}'", populatedFirst);
                break;
            }
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
            }
        }

        if (!autoFilled) {
            logger.info("NPPES lookup did not auto-fill names. Typing manually: '{}', '{}'", firstName, lastName);
            sendKeysToVisible(firstNameInput, firstName, timeout);
            sendKeysToVisible(lastNameInput, lastName, timeout);
        } else {
            // Retrieve auto-filled names and send/dispatch events to sync React/Angular
            // state
            String filledFirst = getAttributeOfVisible(firstNameInput, "value", timeout);
            String filledLast = getAttributeOfVisible(lastNameInput, "value", timeout);
            logger.info("Syncing React/Angular state for auto-filled names: '{}', '{}'", filledFirst, filledLast);
            sendKeysToVisible(firstNameInput, filledFirst, timeout);
            sendKeysToVisible(lastNameInput, filledLast, timeout);
        }

        // Brief sleep to let any pending React event cycle complete
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
    }

    private String getAttributeOfVisible(By locator, String attribute, int timeout) {
        try {
            WebElement el = getVisibleElement(locator, timeout);
            return el.getAttribute(attribute);
        } catch (Exception e) {
            return "";
        }
    }

    private WebElement getVisibleElement(By locator, int timeout) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        List<WebElement> elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
        for (WebElement element : elements) {
            if (element.isDisplayed() && element.isEnabled()) {
                return element;
            }
        }
        throw new RuntimeException("No visible element found for locator: " + locator);
    }

    private void sendKeysToVisible(By locator, String value, int timeout) {
        WebElement visibleElement = getVisibleElement(locator, timeout);
        visibleElement.clear();
        visibleElement.sendKeys(value);

        // Dispatch React/Angular input events to ensure form state updates
        try {
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                    "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                            "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));" +
                            "arguments[0].dispatchEvent(new Event('blur', { bubbles: true }));",
                    visibleElement);
        } catch (Exception e) {
            logger.warn("Failed to dispatch JS events on locator {}: {}", locator, e.getMessage());
        }

        logger.info("Successfully typed '{}' in visible element {}", value, locator);
    }

    public void clickAdd() {
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));
        try {
            logger.info("Waiting for Add button to be clickable...");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            wait.until(ExpectedConditions.elementToBeClickable(addButton));
            logger.info("Add button is enabled, performing robust click...");
            elementUtils.click(addButton, timeout);
        } catch (Exception e) {
            logger.warn("Add button not clickable after wait. Performing JS click fallback: {}", e.getMessage());
            try {
                WebElement element = driver.findElement(addButton);
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
            } catch (Exception ex) {
                // ignore
            }
        }
    }

    public boolean isNpiErrorDisplayed() {
        return elementUtils.isDisplayed(npiError, 2);
    }

    public String getNpiErrorText() {
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));
        return elementUtils.getText(npiError, timeout);
    }
}
