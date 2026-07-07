package com.automation.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class ElementUtils {
    private static final Logger logger = LogManager.getLogger(ElementUtils.class);
    private final WebDriver driver;

    public ElementUtils(WebDriver driver) {
        this.driver = driver;
    }

    public WebElement waitForElementToBeVisible(By locator, int timeoutInSeconds) {
        try {
            logger.info("Waiting for element {} to be visible (Timeout: {}s)", locator, timeoutInSeconds);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (Exception e) {
            logger.error("Element {} was not visible within {}s", locator, timeoutInSeconds, e);
            throw e;
        }
    }

    public WebElement waitForElementToBeClickable(By locator, int timeoutInSeconds) {
        try {
            logger.info("Waiting for element {} to be clickable (Timeout: {}s)", locator, timeoutInSeconds);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
            return wait.until(ExpectedConditions.elementToBeClickable(locator));
        } catch (Exception e) {
            logger.error("Element {} was not clickable within {}s", locator, timeoutInSeconds, e);
            throw e;
        }
    }

    public void click(By locator, int timeoutInSeconds) {
        for (int i = 0; i < 3; i++) {
            try {
                WebElement element = waitForElementToBeClickable(locator, timeoutInSeconds);
                logger.info("Clicking on element {}", locator);
                try {
                    // Scroll to center of viewport to prevent header/footer overlays
                    ((org.openqa.selenium.JavascriptExecutor) driver)
                            .executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
                    Thread.sleep(150); // Short sleep for scroll completion
                } catch (Exception e) {
                    // ignore scroll errors
                }
                try {
                    element.click();
                } catch (Exception e) {
                    logger.warn("Standard click failed on {}, retrying using Actions click...", locator);
                    try {
                        new org.openqa.selenium.interactions.Actions(driver).moveToElement(element).click().perform();
                    } catch (Exception ex) {
                        logger.error("Actions click also failed on {}. Trying JavaScript click as last resort...",
                                locator);
                        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();",
                                element);
                    }
                }
                return;
            } catch (org.openqa.selenium.StaleElementReferenceException e) {
                logger.warn("Stale element reference for {} on click attempt {}. Retrying...", locator, i + 1);
                try {
                    Thread.sleep(500);
                } catch (Exception ex) {
                }
            }
        }
        // Final attempt letting exception propagate if all retries failed
        WebElement element = waitForElementToBeClickable(locator, timeoutInSeconds);
        element.click();
    }

    public void sendKeys(By locator, String text, int timeoutInSeconds) {
        logger.info("Clearing and typing '{}' in element {}", text, locator);
        for (int i = 0; i < 3; i++) {
            try {
                WebElement element = waitForElementToBeVisible(locator, timeoutInSeconds);
                String type = element.getAttribute("type");
                if (type != null && type.equalsIgnoreCase("date")) {
                    String jsValue = text;
                    if (text.length() == 8 && text.matches("\\d+")) {
                        jsValue = text.substring(4) + "-" + text.substring(0, 2) + "-" + text.substring(2, 4);
                    }
                    logger.info("Setting date value via JS: {}", jsValue);
                    ((org.openqa.selenium.JavascriptExecutor) driver)
                            .executeScript("arguments[0].value = arguments[1];", element, jsValue);
                    ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                            "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));", element);
                    ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                            "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", element);
                } else {
                    element.clear();
                    try {
                        element.sendKeys(org.openqa.selenium.Keys.chord(getSelectAllModifier(), "a"),
                                org.openqa.selenium.Keys.BACK_SPACE);
                    } catch (Exception ex) {
                    }
                    element.sendKeys(text);
                }
                return;
            } catch (org.openqa.selenium.StaleElementReferenceException e) {
                logger.warn("Stale element reference for {} on attempt {}. Retrying...", locator, i + 1);
                try {
                    Thread.sleep(500);
                } catch (Exception ex) {
                }
            }
        }
        // Final attempt letting exception propagate if all retries failed
        WebElement element = waitForElementToBeVisible(locator, timeoutInSeconds);
        String type = element.getAttribute("type");
        if (type != null && type.equalsIgnoreCase("date")) {
            String jsValue = text;
            if (text.length() == 8 && text.matches("\\d+")) {
                jsValue = text.substring(4) + "-" + text.substring(0, 2) + "-" + text.substring(2, 4);
            }
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];",
                    element, jsValue);
            ((org.openqa.selenium.JavascriptExecutor) driver)
                    .executeScript("arguments[0].dispatchEvent(new Event('input', { bubbles: true }));", element);
            ((org.openqa.selenium.JavascriptExecutor) driver)
                    .executeScript("arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", element);
        } else {
            element.clear();
            try {
                element.sendKeys(org.openqa.selenium.Keys.chord(getSelectAllModifier(), "a"),
                        org.openqa.selenium.Keys.BACK_SPACE);
            } catch (Exception ex) {
            }
            element.sendKeys(text);
        }
    }

    public String getText(By locator, int timeoutInSeconds) {
        WebElement element = waitForElementToBeVisible(locator, timeoutInSeconds);
        String text = element.getText();
        logger.info("Retrieved text '{}' from element {}", text, locator);
        return text;
    }

    public boolean isDisplayed(By locator, int timeoutInSeconds) {
        try {
            logger.info("Checking visibility of element {}", locator);
            WebElement element = waitForElementToBeVisible(locator, timeoutInSeconds);
            return element.isDisplayed();
        } catch (Exception e) {
            logger.warn("Element {} is not displayed", locator);
            return false;
        }
    }

    /**
     * Helper to select an option from a custom dropdown list
     */
    public void selectFromCustomDropdown(By dropdownLocator, By optionsLocator, String optionText,
            int timeoutInSeconds) {
        // Check if the desired option is already selected
        try {
            List<WebElement> triggers = driver.findElements(dropdownLocator);
            if (!triggers.isEmpty()) {
                String currentText = triggers.get(0).getText().trim();
                if (currentText.equalsIgnoreCase(optionText) || currentText.contains(optionText)) {
                    logger.info("Dropdown {} is already selected with '{}'. Skipping selection.", dropdownLocator,
                            currentText);
                    return;
                }
            }
        } catch (Exception e) {
            logger.warn("Error checking current dropdown selection: {}", e.getMessage());
        }

        logger.info("Opening dropdown {} and selecting option containing '{}'", dropdownLocator, optionText);

        // Find matching dropdown elements and log their details for debugging
        try {
            List<WebElement> triggers = driver.findElements(dropdownLocator);
            logger.info("Found {} potential dropdown trigger elements matching {}", triggers.size(), dropdownLocator);
            for (int i = 0; i < triggers.size(); i++) {
                WebElement t = triggers.get(i);
                logger.info("Trigger [{}]: Tag='{}', Text='{}', Class='{}', Displayed={}",
                        i, t.getTagName(), t.getText(), t.getAttribute("class"), t.isDisplayed());
            }
        } catch (Exception e) {
            logger.warn("Failed to log dropdown trigger details: {}", e.getMessage());
        }

        // Try opening the dropdown with retry logic
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        boolean opened = false;

        for (int i = 0; i < 3; i++) {
            try {
                logger.info("Attempt {} to click dropdown trigger...", i + 1);
                click(dropdownLocator, 5);

                // Wait briefly for the options to be visible
                wait.until(ExpectedConditions.visibilityOfElementLocated(optionsLocator));
                opened = true;
                logger.info("Dropdown opened successfully. Option is now visible.");
                break;
            } catch (Exception e) {
                logger.warn("Option not visible after click attempt {}. Retrying...", i + 1);
            }
        }

        if (!opened) {
            logger.warn("Dropdown option was not visible after retries. Doing one final click with full timeout...");
            click(dropdownLocator, timeoutInSeconds);
        }

        // Retrieve and log options for debugging with retry on stale element reference
        WebDriverWait fullWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
        boolean found = false;

        for (int attempt = 0; attempt < 3; attempt++) {
            try {
                List<WebElement> options = fullWait
                        .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(optionsLocator));
                logger.info("Found {} option elements matching {} (Attempt {})", options.size(), optionsLocator,
                        attempt + 1);

                for (WebElement option : options) {
                    try {
                        String text = option.getText().trim();
                        logger.info("Checking option: Text='{}', Displayed={}", text, option.isDisplayed());
                        if (option.isDisplayed() && (text.contains(optionText) || optionText.contains(text))) {
                            logger.info("Clicking option containing '{}'", optionText);
                            clickElement(option);
                            found = true;
                            break;
                        }
                    } catch (org.openqa.selenium.StaleElementReferenceException e) {
                        logger.warn("StaleElementReferenceException during option loop check: {}", e.getMessage());
                        throw e;
                    }
                }
                if (found) {
                    break;
                }
            } catch (org.openqa.selenium.StaleElementReferenceException e) {
                logger.warn("Stale element reference encountered during dropdown selection. Retrying...", e);
                try {
                    Thread.sleep(500);
                } catch (Exception ex) {
                }
            }
        }

        if (!found) {
            logger.error("Failed to find and click option containing '{}'", optionText);
            throw new RuntimeException("Option '" + optionText + "' not found in dropdown " + dropdownLocator);
        }
    }

    public void clickElement(WebElement element) {
        try {
            // Scroll to center of viewport to prevent header/footer overlays
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
            Thread.sleep(150); // Short sleep for scroll completion
        } catch (Exception e) {
            // ignore scroll errors
        }
        try {
            element.click();
        } catch (Exception e) {
            logger.warn("Standard click failed on web element, retrying using Actions click...", e);
            try {
                new org.openqa.selenium.interactions.Actions(driver).moveToElement(element).click().perform();
            } catch (Exception ex) {
                logger.error("Actions click also failed. Trying JavaScript click as last resort...");
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
            }
        }
    }

    public void clickUsingJS(By locator, int timeoutInSeconds) {
        WebElement element = waitForElementToBeVisible(locator, timeoutInSeconds);
        logger.info("Clicking on element {} using JavaScript", locator);
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    private org.openqa.selenium.Keys getSelectAllModifier() {
        return System.getProperty("os.name").toLowerCase().contains("mac")
                ? org.openqa.selenium.Keys.COMMAND
                : org.openqa.selenium.Keys.CONTROL;
    }
}
