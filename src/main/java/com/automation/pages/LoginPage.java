package com.automation.pages;

import com.automation.base.BasePage;
import com.automation.utils.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    // Locators
    private final By passwordTabButton = By.xpath("//button[contains(., 'Password')] | //span[contains(text(), 'Password')] | //*[text()='Password']");
    private final By emailInput = By.xpath("//input[@type='email' or @placeholder='email@example.com' or contains(@placeholder, 'email')]");
    private final By passwordInput = By.xpath("//input[@type='password' or @placeholder='Enter Password' or contains(@placeholder, 'Password')]");
    private final By signInButton = By.xpath("//button[contains(., 'Sign In') or contains(., 'Sign in') or contains(@class, 'Sign')]");
    
    // For assertions/negative scenarios
    private final By errorMessage = By.xpath("//div[contains(@class, 'error') or contains(@class, 'alert')] | //*[contains(text(), 'Invalid') or contains(text(), 'invalid') or contains(text(), 'failed') or contains(text(), 'incorrect') or contains(text(), 'user id')]");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void clickPasswordTab() {
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));
        elementUtils.click(passwordTabButton, timeout);
    }

    public void enterEmail(String email) {
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));
        elementUtils.sendKeys(emailInput, email, timeout);
    }

    public void enterPassword(String password) {
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));
        elementUtils.sendKeys(passwordInput, password, timeout);
    }

    public void clickSignIn() {
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));
        elementUtils.click(signInButton, timeout);
    }

    /**
     * Performs standard login workflow using username and password
     */
    public void login(String email, String password) {
        clickPasswordTab();
        enterEmail(email);
        enterPassword(password);
        clickSignIn();
    }

    public boolean isErrorMessageDisplayed() {
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));
        return elementUtils.isDisplayed(errorMessage, timeout);
    }

    public String getErrorMessageText() {
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));
        return elementUtils.getText(errorMessage, timeout);
    }
}
