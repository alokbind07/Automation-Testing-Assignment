package com.automation.pages;

import com.automation.base.BasePage;
import com.automation.utils.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebElement;
import java.time.Duration;
import java.util.List;

public class ProvidersPage extends BasePage {
    private static final Logger logger = LogManager.getLogger(ProvidersPage.class);

    // Locators
    private final By locationDropdown = By.xpath("//*[contains(text(), 'Locations') or contains(text(), 'Knight') or contains(text(), 'Oso')]");
    // Options in the location dropdown list
    private final By locationOptions = By.xpath("//div[contains(@class, 'option') or contains(@class, 'dropdown') or contains(@class, 'list')]//*[contains(text(), 'Dark Knight') or contains(text(), 'Oso')] | //*[contains(text(), 'Dark Knight Oso Inc')]");
    // "+ Add Provider" button
    private final By addProviderButton = By.xpath("//button[contains(., 'Add Provider')] | //span[contains(text(), 'Add Provider')] | //*[contains(text(), 'Add Provider') and contains(@class, 'button')]");
    // Indicator for page load verification
    private final By activeProvidersTab = By.xpath("//*[contains(text(), 'Active Providers')]");

    public ProvidersPage(WebDriver driver) {
        super(driver);
    }

    public boolean isPageLoaded() {
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));
        return elementUtils.isDisplayed(activeProvidersTab, timeout);
    }

    public void selectLocation(String locationName) {
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));
        // We use the custom dropdown helper from ElementUtils
        // It clicks the dropdown container, waits for options to load, and clicks the target option containing locationName
        By locationOptionLocator = By.xpath("//*[contains(text(), '" + locationName + "')]");
        elementUtils.selectFromCustomDropdown(locationDropdown, locationOptionLocator, locationName, timeout);
        
        // Wait until location dropdown shows the selected location name
        logger.info("Waiting for location dropdown trigger text to update to '{}'...", locationName);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        wait.until(ExpectedConditions.textToBePresentInElementLocated(locationDropdown, locationName));
        logger.info("Location successfully selected and trigger text updated.");
    }

    public void clickAddProvider() {
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));
        By modalIndicator = By.xpath("//*[contains(text(), 'Add Provider') and contains(@class, 'modal-title')] | //*[contains(text(), 'Select Onboarding Workflow')] | //*[contains(text(), 'Step 1/2')]");
        
        // Retry clicking if React state swallows the click during page loading transitions
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        boolean opened = false;
        
        for (int i = 0; i < 3; i++) {
            try {
                logger.info("Attempt {} to click + Add Provider button...", i + 1);
                elementUtils.click(addProviderButton, 5);
                wait.until(ExpectedConditions.visibilityOfElementLocated(modalIndicator));
                opened = true;
                logger.info("Add Provider modal opened successfully.");
                break;
            } catch (Exception e) {
                logger.warn("Modal did not open on attempt {}. Retrying click...", i + 1);
            }
        }
        
        if (!opened) {
            logger.warn("Modal not open after retries. Doing one final click with full timeout...");
            elementUtils.click(addProviderButton, timeout);
            WebDriverWait finalWait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
            finalWait.until(ExpectedConditions.visibilityOfElementLocated(modalIndicator));
        }
    }

    public void deleteProviderIfInProcess(String npi) {
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));
        try {
            logger.info("Checking if provider with NPI {} is in In Process list...", npi);
            elementUtils.click(By.id("nav-process-tab"), 5);
            
            // Wait up to 10 seconds for the table rows text to load/populate
            long start = System.currentTimeMillis();
            boolean loaded = false;
            while (System.currentTimeMillis() - start < 10000) {
                List<WebElement> dataRows = driver.findElements(By.xpath("//tbody/tr"));
                if (dataRows.size() > 0) {
                    for (WebElement row : dataRows) {
                        String txt = row.getText().trim();
                        if (!txt.isEmpty() && (txt.contains("@") || txt.contains("No data") || txt.contains("No Providers") || txt.contains("-"))) {
                            loaded = true;
                            break;
                        }
                    }
                }
                if (loaded) break;
                Thread.sleep(500);
            }
            
            List<WebElement> rows = driver.findElements(By.xpath("//table//tr | //tr"));
            logger.info("Found {} total rows in In Process table", rows.size());
            for (int r = 0; r < rows.size(); r++) {
                logger.info("Row {}: {}", r, rows.get(r).getText().replace("\n", " | "));
            }
            
            By rowDeleteBtn = By.xpath("//tr[td[contains(., '" + npi + "')]]//a[p[@mattooltip='Delete Provider']] | //tr[td[contains(., '" + npi + "')]]//img[contains(@src, 'Delete') or contains(@src, 'delete')]/parent::a | //tr[contains(., '" + npi + "')]//a[contains(., 'Delete') or .//img[contains(@src, 'delete')]]");
            
            if (driver.findElements(rowDeleteBtn).size() > 0) {
                logger.info("Provider with NPI {} found in In Process list. Deleting...", npi);
                elementUtils.click(rowDeleteBtn, timeout);
                Thread.sleep(1500);
                
                By confirmDeleteBtn = By.xpath("//div[@id='deleteModel']//button[contains(@class, 'btnmarkdelete')] | //button[contains(@class, 'btnmarkdelete')]");
                elementUtils.click(confirmDeleteBtn, timeout);
                logger.info("Delete confirmation clicked.");
                Thread.sleep(3000);
            } else {
                logger.info("Provider with NPI {} not found in In Process list.", npi);
            }
            
            // Switch back to Active Providers tab
            elementUtils.click(By.xpath("//button[contains(., 'Active Providers')] | //button[contains(text(), 'Active')]"), 5);
            Thread.sleep(1000);
        } catch (Exception e) {
            logger.warn("Exception during deleteProviderIfInProcess: {}", e.getMessage());
            try {
                driver.findElement(By.xpath("//button[contains(., 'Active Providers')] | //button[contains(text(), 'Active')]")).click();
            } catch (Exception ex) {}
        }
    }
}
