package com.automation.tests;

import com.automation.utils.ConfigReader;
import com.automation.utils.ReportUtils;
import com.aventstack.extentreports.MediaEntityBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.lang.reflect.Method;
import java.time.Duration;

public class BaseTest {
    private static final Logger logger = LogManager.getLogger(BaseTest.class);
    protected WebDriver driver;

    @BeforeSuite
    public void setupSuite() {
        ReportUtils.initReport();
        logger.info("Test suite configuration initialized.");
    }

    @BeforeMethod
    public void setupTest(Method method) {
        String testName = method.getName();
        ReportUtils.startTest(testName, "Execution details for " + testName);
        
        String browser = ConfigReader.getProperty("browser").toLowerCase();
        int timeout = Integer.parseInt(ConfigReader.getProperty("timeout"));
        
        String headlessProp = System.getProperty("headless");
        if (headlessProp == null) {
            headlessProp = ConfigReader.getProperty("headless");
        }
        boolean headless = "true".equalsIgnoreCase(headlessProp);
        
        logger.info("Initializing browser: {} (Headless: {})", browser, headless);
        switch (browser) {
            case "chrome" -> {
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--start-maximized");
                options.addArguments("--disable-notifications");
                options.addArguments("--disable-popup-blocking");
                if (headless) {
                    options.addArguments("--headless=new");
                    options.addArguments("--window-size=1920,1080");
                    options.addArguments("--disable-gpu");
                }
                driver = new ChromeDriver(options);
                if (!headless) {
                    driver.manage().window().maximize();
                } else {
                    driver.manage().window().setSize(new org.openqa.selenium.Dimension(1920, 1080));
                }
            }
            case "firefox" -> {
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions options = new FirefoxOptions();
                if (headless) {
                    options.addArguments("-headless");
                    options.addArguments("--width=1920");
                    options.addArguments("--height=1080");
                }
                driver = new FirefoxDriver(options);
                if (!headless) {
                    driver.manage().window().maximize();
                } else {
                    driver.manage().window().setSize(new org.openqa.selenium.Dimension(1920, 1080));
                }
            }
            case "edge" -> {
                WebDriverManager.edgedriver().setup();
                EdgeOptions options = new EdgeOptions();
                options.addArguments("--start-maximized");
                if (headless) {
                    options.addArguments("--headless=new");
                    options.addArguments("--window-size=1920,1080");
                    options.addArguments("--disable-gpu");
                }
                driver = new EdgeDriver(options);
                if (!headless) {
                    driver.manage().window().maximize();
                } else {
                    driver.manage().window().setSize(new org.openqa.selenium.Dimension(1920, 1080));
                }
            }
            default -> {
                logger.error("Unsupported browser type in config: {}", browser);
                throw new IllegalArgumentException("Unsupported browser: " + browser);
            }
        }
        
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0)); // We use explicit waits only
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        
        String url = ConfigReader.getProperty("url");
        logger.info("Navigating to URL: {}", url);
        driver.get(url);
    }

    @AfterMethod
    public void tearDownTest(ITestResult result) {
        if (driver != null) {
            try {
                if (result.getStatus() == ITestResult.FAILURE) {
                    logger.error("Test failed: {}", result.getName());
                    String screenshotPath = ReportUtils.captureScreenshot(driver, result.getName());
                    if (screenshotPath != null) {
                        ReportUtils.getTest().fail("Test Failed. View screenshot below:", 
                            MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
                    }
                    ReportUtils.logFail(result.getThrowable().getMessage(), result.getThrowable());
                } else if (result.getStatus() == ITestResult.SKIP) {
                    logger.warn("Test skipped: {}", result.getName());
                    ReportUtils.getTest().skip("Test Skipped: " + result.getThrowable().getMessage());
                } else {
                    logger.info("Test passed: {}", result.getName());
                    ReportUtils.logPass("Test execution completed successfully.");
                }
            } catch (Exception e) {
                logger.error("Exception in AfterMethod teardown reporting step", e);
            } finally {
                logger.info("Closing browser session...");
                driver.quit();
            }
        }
    }

    @AfterSuite
    public void tearDownSuite() {
        ReportUtils.flushReport();
        logger.info("Test suite reporting flushed and complete.");
    }
}
