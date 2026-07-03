package com.automation.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportUtils {
    private static final Logger logger = LogManager.getLogger(ReportUtils.class);
    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    public static synchronized ExtentReports initReport() {
        if (extent == null) {
            logger.info("Initializing ExtentReports...");
            String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
            String reportPath = System.getProperty("user.dir") + "/reports/ExtentReport_" + timeStamp + ".html";
            
            ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
            spark.config().setTheme(Theme.DARK);
            spark.config().setDocumentTitle("QA Automation Execution Report");
            spark.config().setReportName("Provider Passport Onboarding Tests");
            
            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("Environment", "Staging");
            extent.setSystemInfo("User", "QA Automation Engineer");
            extent.setSystemInfo("OS", System.getProperty("os.name"));
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
        }
        return extent;
    }

    public static synchronized void flushReport() {
        if (extent != null) {
            logger.info("Flushing ExtentReports...");
            extent.flush();
        }
    }

    public static synchronized void startTest(String testName, String description) {
        ExtentTest test = extent.createTest(testName, description);
        extentTest.set(test);
        logger.info("Started report test: '{}'", testName);
    }

    public static ExtentTest getTest() {
        return extentTest.get();
    }

    public static void logInfo(String message) {
        logger.info(message);
        if (getTest() != null) {
            getTest().log(Status.INFO, message);
        }
    }

    public static void logPass(String message) {
        logger.info("[PASS] " + message);
        if (getTest() != null) {
            getTest().log(Status.PASS, message);
        }
    }

    public static void logFail(String message, Throwable throwable) {
        logger.error("[FAIL] " + message, throwable);
        if (getTest() != null) {
            getTest().log(Status.FAIL, message);
            if (throwable != null) {
                getTest().fail(throwable);
            }
        }
    }

    public static String captureScreenshot(WebDriver driver, String screenshotName) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = screenshotName + "_" + timeStamp + ".png";
        String directory = System.getProperty("user.dir") + "/screenshots/";
        
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        String path = directory + fileName;
        try {
            File sourceFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destinationFile = new File(path);
            java.nio.file.Files.copy(sourceFile.toPath(), destinationFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            logger.info("Screenshot captured and saved to: {}", path);
            return path;
        } catch (IOException e) {
            logger.error("Failed to capture screenshot: {}", screenshotName, e);
            return null;
        }
    }
}
