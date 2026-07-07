package com.automation.tests;

import com.automation.pages.LoginPage;
import com.automation.pages.DashboardPage;
import com.automation.utils.ConfigReader;
import com.automation.utils.ReportUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.Random;

public class LoginTest extends BaseTest {

        // 1. Valid Email + Valid Password (Happy Path)
        @Test
        public void testSuccessfulLogin() {
                ReportUtils.logInfo("Running positive test case: Login with valid credentials...");
                String email = ConfigReader.getProperty("email");
                String password = ConfigReader.getProperty("password");

                LoginPage loginPage = new LoginPage(driver);
                ReportUtils.logInfo("Logging in with user: " + email);
                loginPage.login(email, password);

                DashboardPage dashboardPage = new DashboardPage(driver);
                ReportUtils.logInfo("Verifying successful login...");
                Assert.assertTrue(dashboardPage.isDashboardLoaded(), "Dashboard failed to load!");
                ReportUtils.logPass("Successfully logged in and reached the Dashboard.");
        }

        // 2. Invalid Email + Valid Password
        @Test
        public void testFailedLoginWithInvalidEmailAndValidPassword() {
                ReportUtils.logInfo("Running negative test case: Login with invalid email and valid password...");
                LoginPage loginPage = new LoginPage(driver);

                String invalidEmail = "invalid_user_" + new Random().nextInt(10000) + "@gmail.com";
                String validPassword = ConfigReader.getProperty("password");

                ReportUtils.logInfo("Attempting login with invalid email: " + invalidEmail + " and correct password.");
                loginPage.login(invalidEmail, validPassword);

                verifyLoginFailure(loginPage, "invalid email with valid password");
        }

        // 3. Valid Email + Invalid Password
        @Test
        public void testFailedLoginWithValidEmailAndInvalidPassword() {
                ReportUtils.logInfo("Running negative test case: Login with valid email and invalid password...");
                LoginPage loginPage = new LoginPage(driver);

                String validEmail = ConfigReader.getProperty("email");
                String invalidPassword = "WrongPassword123!";

                ReportUtils.logInfo("Attempting login with correct email: " + validEmail + " and invalid password.");
                loginPage.login(validEmail, invalidPassword);

                verifyLoginFailure(loginPage, "valid email with invalid password");
        }

        // 4. Invalid Email + Invalid Password
        @Test
        public void testFailedLoginWithInvalidCredentials() {
                ReportUtils.logInfo("Running negative test case: Login with invalid credentials...");
                LoginPage loginPage = new LoginPage(driver);

                String invalidEmail = "invalid_user_" + new Random().nextInt(10000) + "@gmail.com";
                String invalidPassword = "WrongPassword123!";

                ReportUtils.logInfo("Attempting login with invalid email: " + invalidEmail + " and invalid password.");
                loginPage.login(invalidEmail, invalidPassword);

                verifyLoginFailure(loginPage, "invalid credentials");
        }

        // 5. Valid Email + Blank Password
        @Test
        public void testFailedLoginWithValidEmailAndBlankPassword() {
                ReportUtils.logInfo("Running negative test case: Login with correct username and blank password...");
                LoginPage loginPage = new LoginPage(driver);

                String validEmail = ConfigReader.getProperty("email");
                String blankPassword = "";

                ReportUtils.logInfo("Attempting login with correct email: " + validEmail + " and blank password.");
                loginPage.login(validEmail, blankPassword);

                verifyLoginFailure(loginPage, "blank password");
        }

        // 6. Blank Email + Valid Password
        @Test
        public void testFailedLoginWithBlankEmailAndValidPassword() {
                ReportUtils.logInfo("Running negative test case: Login with blank email and valid password...");
                LoginPage loginPage = new LoginPage(driver);

                String blankEmail = "";
                String validPassword = ConfigReader.getProperty("password");

                ReportUtils.logInfo("Attempting login with blank email and valid password.");
                loginPage.login(blankEmail, validPassword);

                verifyLoginFailure(loginPage, "blank email");
        }

        // 7. Blank Email + Blank Password
        @Test
        public void testFailedLoginWithBlankEmailAndBlankPassword() {
                ReportUtils.logInfo("Running negative test case: Login with blank email and blank password...");
                LoginPage loginPage = new LoginPage(driver);

                ReportUtils.logInfo("Attempting login with blank email and blank password.");
                loginPage.login("", "");

                verifyLoginFailure(loginPage, "blank credentials");
        }

        // 8. Invalid Email Format (e.g. no @ sign or domain) + Valid Password
        @Test
        public void testFailedLoginWithInvalidEmailFormat() {
                ReportUtils.logInfo(
                                "Running negative test case: Login with invalid email format and valid password...");
                LoginPage loginPage = new LoginPage(driver);

                String invalidFormatEmail = "notanemailaddress";
                String validPassword = ConfigReader.getProperty("password");

                ReportUtils.logInfo("Attempting login with invalid format email: " + invalidFormatEmail
                                + " and valid password.");
                loginPage.login(invalidFormatEmail, validPassword);

                verifyLoginFailure(loginPage, "invalid email format");
        }

        // 9. SQL Injection Input in Email Field + Valid Password
        @Test
        public void testFailedLoginWithSqlInjectionEmail() {
                ReportUtils.logInfo("Running negative test case: Login with SQL Injection attempt in email field...");
                LoginPage loginPage = new LoginPage(driver);

                String sqlInjectionEmail = "' OR '1'='1' --";
                String validPassword = ConfigReader.getProperty("password");

                ReportUtils.logInfo("Attempting login with SQL Injection email: " + sqlInjectionEmail
                                + " and valid password.");
                loginPage.login(sqlInjectionEmail, validPassword);

                verifyLoginFailure(loginPage, "SQL injection string");
        }

        // 10. Boundary Value Testing (Extremely Long Inputs)
        @Test
        public void testFailedLoginWithLongInputs() {
                ReportUtils.logInfo(
                                "Running negative test case: Login with extremely long email and password inputs...");
                LoginPage loginPage = new LoginPage(driver);

                // Generate a 500 character long string
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < 50; i++) {
                        builder.append("longemailstring");
                }
                builder.append("@gmail.com");
                String longEmail = builder.toString();
                String longPassword = "pw".repeat(100);

                ReportUtils.logInfo("Attempting login with extremely long credentials (500+ chars).");
                loginPage.login(longEmail, longPassword);

                verifyLoginFailure(loginPage, "extremely long inputs");
        }

        /**
         * Shared verification helper for login failure tests
         */
        private void verifyLoginFailure(LoginPage loginPage, String testScenarioDescription) {
                ReportUtils.logInfo("Verifying error message is displayed on login page...");
                Assert.assertTrue(loginPage.isErrorMessageDisplayed(),
                                "Error message was not displayed for: " + testScenarioDescription);

                String actualError = loginPage.getErrorMessageText();
                ReportUtils.logInfo("Actual error message displayed: '" + actualError + "'");
                Assert.assertNotNull(actualError, "Error message text is null!");
                Assert.assertFalse(actualError.trim().isEmpty(), "Error message text is empty!");

                ReportUtils.logPass(
                                "Negative login test passed. Login failed as expected, and error message was correctly displayed: '"
                                                + actualError + "'");
        }
}
