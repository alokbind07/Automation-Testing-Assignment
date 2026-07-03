package com.automation.tests;

import com.automation.pages.*;
import com.automation.pages.onboarding.*;
import com.automation.utils.ConfigReader;
import com.automation.utils.ReportUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Random;

public class OnboardingTest extends BaseTest {

    public static String generateValidNPI() {
        Random rand = new Random();
        int[] digits = new int[9];
        digits[0] = 1;
        for (int i = 1; i < 9; i++) {
            digits[i] = rand.nextInt(10);
        }
        
        int sum = 24; // 8 (from prefix index 0) + 0 (index 1 * 2) + 8 (index 2) + 8 (index 3 * 2) + 0 (index 4)
        
        for (int i = 0; i < 9; i++) {
            int val = digits[i];
            if (i % 2 == 0) {
                val = val * 2;
                if (val >= 10) {
                    val = val - 9;
                }
            }
            sum += val;
        }
        
        int checkDigit = (10 - (sum % 10)) % 10;
        StringBuilder sb = new StringBuilder();
        for (int d : digits) {
            sb.append(d);
        }
        sb.append(checkDigit);
        return sb.toString();
    }

    @Test
    public void testSuccessfulOnboarding() throws Exception {
        String email = ConfigReader.getProperty("email");
        String password = ConfigReader.getProperty("password");
        String npi = ConfigReader.getProperty("npi");
        if (npi == null || npi.trim().isEmpty()) {
            npi = "1780658039";
        }
        
        LoginPage loginPage = new LoginPage(driver);
        ReportUtils.logInfo("Logging in with user: " + email);
        loginPage.login(email, password);
        
        DashboardPage dashboardPage = new DashboardPage(driver);
        ReportUtils.logInfo("Verifying successful login...");
        Assert.assertTrue(dashboardPage.isDashboardLoaded(), "Dashboard failed to load!");
        ReportUtils.logPass("Successfully logged in and reached the Dashboard.");
        
        ReportUtils.logInfo("Navigating to Staff -> Providers...");
        dashboardPage.navigateToProviders();
        
        ProvidersPage providersPage = new ProvidersPage(driver);
        Assert.assertTrue(providersPage.isPageLoaded(), "Providers page failed to load!");
        ReportUtils.logPass("Navigated to Providers page.");
        
        // Ensure duplicate in-process provider draft with our target NPI is cleaned up globally
        providersPage.deleteProviderIfInProcess(npi);
        
        String locationName = ConfigReader.getProperty("locationName");
        ReportUtils.logInfo("Selecting location: " + locationName);
        providersPage.selectLocation(locationName);
        ReportUtils.logPass("Location selected successfully.");
        
        ReportUtils.logInfo("Clicking + Add Provider...");
        providersPage.clickAddProvider();
        
        AddProviderModal addProviderModal = new AddProviderModal(driver);
        String workflowName = ConfigReader.getProperty("workflowName");
        ReportUtils.logInfo("Selecting onboarding workflow: " + workflowName);
        addProviderModal.selectWorkflow(workflowName);
        
        ReportUtils.logInfo("Selecting 'Add Provider' option...");
        addProviderModal.clickAddProviderCard();
        
        ReportUtils.logInfo("Clicking Next...");
        addProviderModal.clickNext();
        ReportUtils.logPass("Successfully configured workflow and moved to details step.");
        
        ReportUtils.logInfo("Selecting first privilege option...");
        addProviderModal.selectFirstPrivilege();
        
        String originalWindow = driver.getWindowHandle();
        
        int randomNum = new Random().nextInt(90000) + 10000;
        String firstName = "AutoFn" + randomNum;
        String lastName = "AutoLn" + randomNum;
        
        ReportUtils.logInfo("Entering Provider Details. NPI: " + npi + ", First Name: " + firstName + ", Last Name: " + lastName);
        addProviderModal.fillDetails(npi, firstName, lastName);
        
        ReportUtils.logInfo("Submitting Provider Onboarding request for NPI: " + npi);
        addProviderModal.clickAdd();
        
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {}
        
        Assert.assertTrue(driver.getWindowHandles().size() > 1, "Failed to submit onboarding request or open a new tab with NPI: " + npi);
        ReportUtils.logPass("Successfully submitted onboarding request with NPI: " + npi);

        // Ensure the target directory exists for output files
        new File("target").mkdirs();

        ReportUtils.logInfo("Switching to the newly opened onboarding tab...");
        ProviderFormPage providerFormPage = new ProviderFormPage(driver);
        providerFormPage.switchToNewTab(originalWindow);
        Assert.assertTrue(providerFormPage.isFormPageLoaded(), "Provider Form Page failed to load!");
        ReportUtils.logPass("Successfully switched to the Provider Form Page.");
        
        int randomProv = new Random().nextInt(90000) + 10000;
        String ssn = "99900" + (new Random().nextInt(9000) + 1000);
        String emailAddress = "auto_prov_" + randomProv + "@gmail.com";
        String dob = "01011990";
        String phone = "555" + (new Random().nextInt(9000000) + 1000000);
        String gender = "Male";
        String certifyingBoard = "Academy of Lactation";
        
        ReportUtils.logInfo("Filling out Provider Form Details. SSN: " + ssn + ", Email: " + emailAddress + ", DOB: " + dob + ", Phone: " + phone + ", Gender: " + gender + ", Certifying Board: " + certifyingBoard);
        PersonalInformationPage personalInformationPage = new PersonalInformationPage(driver);
        personalInformationPage.fillBasicInformation(ssn, emailAddress, dob, phone, gender, certifyingBoard);
        
        ReportUtils.logInfo("Clicking Save & Next...");
        personalInformationPage.clickSaveAndNext();
        
        ReportUtils.logInfo("Waiting for Additional Basic Information page to load...");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("homeline1")));
        
        try {
            java.nio.file.Files.writeString(java.nio.file.Path.of("target/additional_basic_info.html"), driver.getPageSource());
            ReportUtils.logInfo("Saved page source for Additional Basic Information page to target/additional_basic_info.html");
        } catch (IOException e) {
            ReportUtils.logInfo("Failed to save page source for Additional Basic Information: " + e.getMessage());
        }
        
        ReportUtils.logInfo("Filling Additional Basic Information form details: Street='123 Main St', State='California', City='Los Angeles', Zip='90001'");
        AdditionalBasicInfoPage additionalBasicInfoPage = new AdditionalBasicInfoPage(driver);
        additionalBasicInfoPage.fillAdditionalBasicInformation(
            "123 Main St", "California", "Los Angeles", "90001",
            "United States", "California", "Los Angeles", "United States",
            "Jane Doe", "Friend", "5551234567"
        );
        
        ReportUtils.logInfo("Clicking Additional Save & Next...");
        additionalBasicInfoPage.clickAdditionalSaveAndNext();
        
        ReportUtils.logInfo("Filling Education form details...");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("profSchoolName0")));
        EducationPage educationPage = new EducationPage(driver);
        educationPage.fillEducationInformation(
            "ACADEMY OF ART UNIVERSITY", "Doctor of Medicine", "Medicine", "09012010", "05012014", "California",
            "ACADEMY OF ART UNIVERSITY", "Bachelor of Science", "Biology", "09012006", "05012010", "California",
            "Residency", "Los Angeles General Hospital", "Internal Medicine", "07012014", "06302017"
        );
        
        ReportUtils.logInfo("Clicking Education Save & Next...");
        educationPage.clickEducationSaveAndNext();
        
        ReportUtils.logInfo("Waiting for License page to load...");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("app-state-license")));
        
        try {
            java.nio.file.Files.writeString(java.nio.file.Path.of("target/license.html"), driver.getPageSource());
            ReportUtils.logInfo("Saved page source for License page to target/license.html");
        } catch (IOException e) {
            ReportUtils.logInfo("Failed to save page source for License page: " + e.getMessage());
        }
        
        ReportUtils.logInfo("Filling any incomplete fields on License page...");
        LicensePage licensePage = new LicensePage(driver);
        licensePage.fillIncompleteLicenses();
        
        ReportUtils.logInfo("Clicking Save & Next on License page...");
        licensePage.clickLicenseSaveAndNext();
        
        ReportUtils.logInfo("Waiting for DEA page to load...");
        try { Thread.sleep(2000); } catch(InterruptedException e) {}
        
        ReportUtils.logInfo("Filling any incomplete fields on DEA page...");
        DeaPage deaPage = new DeaPage(driver);
        deaPage.fillIncompleteLicenses();
        
        ReportUtils.logInfo("Clicking Save & Next on DEA page...");
        deaPage.clickDeaSaveAndNext();
        
        ReportUtils.logInfo("Waiting for Affiliations page to load...");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("Employer0")));
        
        ReportUtils.logInfo("Filling Affiliations & Work History form details...");
        AffiliationsPage affiliationsPage = new AffiliationsPage(driver);
        affiliationsPage.fillAffiliationsInformation(
            "California Health Center", "07012017", "California", "Los Angeles", "12312023"
        );
        
        ReportUtils.logInfo("Clicking Affiliations Save & Next...");
        affiliationsPage.clickAffiliationsSaveAndNext();
        
        ReportUtils.logInfo("Waiting for Provider Logins page to load...");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[contains(@id, 'userName')] | //input[contains(@placeholder, 'username')]")));
        
        ReportUtils.logInfo("Filling Portal Logins details...");
        PortalLoginsPage portalLoginsPage = new PortalLoginsPage(driver);
        portalLoginsPage.fillPortalLogins();
        
        ReportUtils.logInfo("Clicking Portal Logins Save & Next...");
        portalLoginsPage.clickLoginsSaveAndNext();
        
        ReportUtils.logInfo("Waiting for Hospital Affiliations page to load...");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("street1")));
        
        ReportUtils.logInfo("Filling Hospital Affiliations details...");
        HospitalAffiliationsPage hospitalAffiliationsPage = new HospitalAffiliationsPage(driver);
        hospitalAffiliationsPage.fillHospitalAffiliationInformation(
            "123 Health Ave", "California", "Los Angeles", "Los Angeles County",
            "Los Angeles General Hospital", "07012017"
        );
        
        ReportUtils.logInfo("Clicking Hospital Save & Next...");
        hospitalAffiliationsPage.clickHospitalSaveAndNext();
        
        ReportUtils.logInfo("Waiting for Provider Privileges page to load...");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@id='flexCheckDefault'] | //input[@type='checkbox' and following-sibling::label[contains(text(), 'CORE PRIVILEGES') or contains(text(), 'Core Privileges')]]")));
        
        ReportUtils.logInfo("Selecting Core Privileges...");
        PrivilegesPage privilegesPage = new PrivilegesPage(driver);
        privilegesPage.selectCorePrivileges();
        
        ReportUtils.logInfo("Clicking Privileges Save & Next...");
        privilegesPage.clickPrivilegesSaveAndNext();
        
        ReportUtils.logInfo("Waiting for Professional Reference page to load...");
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//label[@for='boardCertified11'] | //label[@for='boardCertified12']")));
        
        ReportUtils.logInfo("Selecting 'Enter Professional Reference Details' option...");
        ProfessionalReferencePage professionalReferencePage = new ProfessionalReferencePage(driver);
        professionalReferencePage.selectEnterReferenceDetails();
        
        try { Thread.sleep(3000); } catch(InterruptedException e) {}
        
        try {
            java.nio.file.Files.writeString(java.nio.file.Path.of("target/professional_reference.html"), driver.getPageSource());
            ReportUtils.logInfo("Saved page source for Professional Reference to target/professional_reference.html");
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            java.nio.file.Files.copy(screenshot.toPath(), java.nio.file.Path.of("target/professional_reference.png"), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            ReportUtils.logInfo("Saved screenshot of Professional Reference to target/professional_reference.png");
        } catch (IOException e) {
            ReportUtils.logInfo("Failed to save page source/screenshot for Professional Reference: " + e.getMessage());
        }
        
        ReportUtils.logInfo("Filling Professional Reference details for Reference 1 and Reference 2...");
        professionalReferencePage.fillProfessionalReferenceDetails("Alok Reference One", "Supervisor", "5551234567", "alok1@gmail.com", "Bob Reference Two", "Colleague", "5557654321", "bob2@gmail.com");
        
        try { Thread.sleep(2000); } catch(InterruptedException e) {}
        
        ReportUtils.logInfo("Clicking Professional Reference Save & Next...");
        professionalReferencePage.clickReferenceSaveAndNext();
        
        ReportUtils.logInfo("Waiting for Upload Documents page to load...");
        wait.until(driver -> driver.getCurrentUrl().toLowerCase().contains("upload"));
        try { Thread.sleep(3000); } catch (InterruptedException e) {}
        
        try {
            java.nio.file.Files.writeString(java.nio.file.Path.of("target/upload_documents.html"), driver.getPageSource());
            ReportUtils.logInfo("Saved page source for Upload Documents page to target/upload_documents.html");
        } catch (IOException e) {
            ReportUtils.logInfo("Failed to save page source for Upload Documents: " + e.getMessage());
        }
        
        try {
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            java.nio.file.Files.copy(screenshot.toPath(), java.nio.file.Path.of("target/upload_documents.png"), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            ReportUtils.logInfo("Saved screenshot to target/upload_documents.png");
        } catch (IOException e) {
            ReportUtils.logInfo("Failed to save screenshot: " + e.getMessage());
        }
        
        ReportUtils.logInfo("Selecting 'Not Applicable' for documents and clicking Save & Next...");
        UploadDocumentsPage uploadDocumentsPage = new UploadDocumentsPage(driver);
        uploadDocumentsPage.selectNotApplicableForDocumentsAndSubmit();
        
        ReportUtils.logInfo("Waiting for Attestations page to load...");
        wait.until(driver -> driver.getCurrentUrl().toLowerCase().contains("attestation"));
        try { Thread.sleep(3000); } catch (InterruptedException e) {}
        
        try {
            java.nio.file.Files.writeString(java.nio.file.Path.of("target/attestations.html"), driver.getPageSource());
            ReportUtils.logInfo("Saved page source for Attestations page to target/attestations.html");
        } catch (IOException e) {
            ReportUtils.logInfo("Failed to save page source for Attestations: " + e.getMessage());
        }
        
        try {
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            java.nio.file.Files.copy(screenshot.toPath(), java.nio.file.Path.of("target/attestations.png"), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            ReportUtils.logInfo("Saved screenshot of Attestations page to target/attestations.png");
        } catch (IOException e) {
            ReportUtils.logInfo("Failed to save screenshot of Attestations: " + e.getMessage());
        }

        ReportUtils.logInfo("Filling Attestations (No to all) and clicking first Sign Now...");
        AttestationsPage attestationsPage = new AttestationsPage(driver);
        attestationsPage.fillAttestationsAndOpenSignature();

        try {
            java.nio.file.Files.writeString(java.nio.file.Path.of("target/sign_modal.html"), driver.getPageSource());
            ReportUtils.logInfo("Saved page source for Sign Modal to target/sign_modal.html");
        } catch (IOException e) {
            ReportUtils.logInfo("Failed to save page source for Sign Modal: " + e.getMessage());
        }

        ReportUtils.logInfo("Signing and saving Attestations step...");
        attestationsPage.signAndSaveAttestations(firstName + " " + lastName);
        
        ReportUtils.logPass("Provider Onboarding workflow, form details, and document selections completed and submitted successfully.");
    }

    @Test
    public void testFailedLoginWithInvalidCredentials() {
        ReportUtils.logInfo("Running negative test case: Login with invalid credentials...");
        LoginPage loginPage = new LoginPage(driver);
        
        String invalidEmail = "invalid_user_" + new Random().nextInt(10000) + "@gmail.com";
        String invalidPassword = "WrongPassword123!";
        
        ReportUtils.logInfo("Attempting login with invalid email: " + invalidEmail + " and invalid password.");
        loginPage.login(invalidEmail, invalidPassword);
        
        ReportUtils.logInfo("Verifying error message is displayed on login page...");
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Error message was not displayed for invalid login credentials!");
        
        String actualError = loginPage.getErrorMessageText();
        ReportUtils.logInfo("Actual error message displayed: '" + actualError + "'");
        Assert.assertNotNull(actualError, "Error message text is null!");
        Assert.assertFalse(actualError.trim().isEmpty(), "Error message text is empty!");
        
        ReportUtils.logPass("Negative login test passed. Login failed as expected, and error message was correctly displayed: '" + actualError + "'");
    }
}