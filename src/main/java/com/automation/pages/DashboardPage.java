package com.automation.pages;

import com.automation.base.BasePage;
import com.automation.utils.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DashboardPage extends BasePage {

    // Locators
    private final By staffMenu = By.xpath("//*[contains(text(), 'Staff')]");
    private final By providersSubMenu = By.xpath("//*[contains(text(), 'Providers')]");
    private final By welcomeHeader = By.xpath("//*[text()='Tasks to do'] | //*[text()='Provider Overview'] | //*[contains(text(), 'Alok Bind')]");

    public DashboardPage(WebDriver driver) {
        super(driver);
    }

    public boolean isDashboardLoaded() {
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));
        return elementUtils.isDisplayed(welcomeHeader, timeout);
    }

    public void navigateToProviders() {
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));
        org.openqa.selenium.WebElement staff = elementUtils.waitForElementToBeClickable(staffMenu, timeout);
        
        // Hover over Staff menu item using Actions API
        org.openqa.selenium.interactions.Actions actions = new org.openqa.selenium.interactions.Actions(driver);
        actions.moveToElement(staff).perform();
        
        // Also attempt click to guarantee dropdown trigger
        try {
            staff.click();
        } catch (Exception e) {
            // Ignored if hover already opened the menu
        }
        
        // Click on the Providers sub-menu
        elementUtils.click(providersSubMenu, timeout);
    }
}
