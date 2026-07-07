package com.automation.pages;

import com.automation.base.BasePage;
import com.automation.utils.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

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
        if (elementUtils.isDisplayed(errorMessage, 2)) {
            return true;
        }
        try {
            WebElement email = driver.findElement(emailInput);
            WebElement password = driver.findElement(passwordInput);
            String emailMsg = (String) ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("return arguments[0].validationMessage;", email);
            String passwordMsg = (String) ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("return arguments[0].validationMessage;", password);
            if ((emailMsg != null && !emailMsg.isEmpty()) || (passwordMsg != null && !passwordMsg.isEmpty())) {
                return true;
            }
        } catch (Exception e) {
            // ignore
        }
        return false;
    }

    public String getErrorMessageText() {
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));
        if (elementUtils.isDisplayed(errorMessage, 2)) {
            return elementUtils.getText(errorMessage, timeout);
        }
        try {
            WebElement email = driver.findElement(emailInput);
            WebElement password = driver.findElement(passwordInput);
            String emailMsg = (String) ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("return arguments[0].validationMessage;", email);
            if (emailMsg != null && !emailMsg.isEmpty()) {
                return emailMsg;
            }
            String passwordMsg = (String) ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("return arguments[0].validationMessage;", password);
            if (passwordMsg != null && !passwordMsg.isEmpty()) {
                return passwordMsg;
            }
        } catch (Exception e) {
            // ignore
        }
        return "";
    }
}
