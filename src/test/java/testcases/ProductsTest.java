package testcases;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;
import pages.LoginPage;
import pages.ProductsPage;
import utils.DriverFactory;
import utils.ScreenshotUtil;
import reporting.ExtentReportManager;
import com.aventstack.extentreports.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProductsTest {
    private WebDriver driver;
    private LoginPage loginPage;
    private ProductsPage productsPage;
    private static ExtentReports extent;
    private ExtentTest test;

    @BeforeSuite
    public void beforeSuite() {
        extent = ExtentReportManager.getReporter();
    }

    @BeforeClass
    public void setUp() {
        DriverFactory.setBrowser("edge"); // Or "chrome"
        driver = DriverFactory.getDriver();
        driver.manage().deleteAllCookies();
        driver.get("https://www.saucedemo.com/");
        loginPage = new LoginPage(driver);
        productsPage = new ProductsPage(driver);
        loginPage.login("standard_user", "secret_sauce");
        Assert.assertTrue(driver.getCurrentUrl().contains("inventory"), "Login failed, not on products page.");
    }

    @BeforeMethod
    public void beforeTestMethod(Method method) {
        test = extent.createTest(method.getName());
        test.log(Status.INFO, "Test started: " + method.getName());
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
    }

    @AfterMethod
    public void afterTestMethod(ITestResult result) {
        String screenshotPath = ScreenshotUtil.captureScreenshot(driver, result.getName());
        if (result.getStatus() == ITestResult.FAILURE) {
            test.fail(result.getThrowable());
            test.addScreenCaptureFromPath(screenshotPath);
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            test.pass("Test passed");
            test.addScreenCaptureFromPath(screenshotPath);
        } else if (result.getStatus() == ITestResult.SKIP) {
            test.skip("Test skipped");
        }
        extent.flush();
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
    }

    @Test
    public void testSortByNameAToZ() {
        test.log(Status.INFO, "Sorting by Name (A to Z)");
        productsPage.selectSortOption("Name (A to Z)");
        List<String> names = extractText(productsPage.getProductNames());
        List<String> sorted = new ArrayList<>(names);
        Collections.sort(sorted);
        Assert.assertEquals(names, sorted, "Products should be sorted by name A-Z");
    }

    @Test
    public void testSortByNameZToA() {
        test.log(Status.INFO, "Sorting by Name (Z to A)");
        productsPage.selectSortOption("Name (Z to A)");
        List<String> names = extractText(productsPage.getProductNames());
        List<String> sorted = new ArrayList<>(names);
        Collections.sort(sorted, Collections.reverseOrder());
        Assert.assertEquals(names, sorted, "Products should be sorted by name Z-A");
    }

    @Test
    public void testSortByPriceLowHigh() {
        test.log(Status.INFO, "Sorting by Price (low to high)");
        productsPage.selectSortOption("Price (low to high)");
        List<Double> prices = extractPrices(productsPage.getProductPrices());
        List<Double> sorted = new ArrayList<>(prices);
        Collections.sort(sorted);
        Assert.assertEquals(prices, sorted, "Products should be sorted by price low-high");
    }

    @Test
    public void testSortByPriceHighLow() {
        test.log(Status.INFO, "Sorting by Price (high to low)");
        productsPage.selectSortOption("Price (high to low)");
        List<Double> prices = extractPrices(productsPage.getProductPrices());
        List<Double> sorted = new ArrayList<>(prices);
        sorted.sort(Collections.reverseOrder());
        Assert.assertEquals(prices, sorted, "Products should be sorted by price high-low");
    }
    
    @Test
    public void testInvalidFilterOption() {
        test.log(Status.INFO, "Attempting to select an invalid filter/sort option from dropdown");

        boolean optionSelected = true;
        try {
            productsPage.selectSortOption("Invalid Option"); // Attempt to select a non-existent option
        } catch (Exception e) {
            optionSelected = false;
            test.log(Status.WARNING, "Expected exception for invalid filter: " + e.getMessage());
        }
        
        // Validate outcome
        Assert.assertFalse(optionSelected, "No filter should be applied, and an error should be handled gracefully for invalid filter option.");
    }

    @Test
    public void testProductDetailsVerification() {
        test.log(Status.INFO, "Verifying product details");
        List<WebElement> names = productsPage.getProductNames();
        List<WebElement> prices = productsPage.getProductPrices();
        List<WebElement> images = productsPage.getProductImages();
        Assert.assertFalse(names.isEmpty(), "No product names found.");
        Assert.assertFalse(prices.isEmpty(), "No product prices found.");
        Assert.assertEquals(images.size(), names.size(), "Image and name count mismatch.");
        Assert.assertEquals(prices.size(), names.size(), "Price and name count mismatch.");
    }

    @Test
    public void testSidebarNavigation() {
        test.log(Status.INFO, "Opening sidebar menu...");
        productsPage.openSidebar();

        // Click About and validate redirect
        test.log(Status.INFO, "Clicking About link in sidebar...");
        productsPage.clickAbout();
        Assert.assertTrue(driver.getCurrentUrl().toLowerCase().contains("saucelabs"),
                "About link should redirect to SauceLabs site.");
        test.log(Status.PASS, "Redirected to SauceLabs.");

        // Return to SauceDemo (login page)
        driver.get("https://www.saucedemo.com/");
        test.log(Status.INFO, "Navigated back to SauceDemo login page after About.");

        // Log back in for further sidebar actions
        loginPage.login("standard_user", "secret_sauce");
        Assert.assertTrue(driver.getCurrentUrl().contains("inventory"), "Should return to products page after login.");

        test.log(Status.INFO, "Opening sidebar menu again for reset...");
        productsPage.openSidebar();
        productsPage.clickResetAppState();
        Assert.assertTrue(driver.getCurrentUrl().contains("inventory"),
                "Reset App State should keep user on products page.");
        test.log(Status.PASS, "Reset App State successful.");
        // Visual delay
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
    }

    @Test
    public void testUIResponsiveness() {
        test.log(Status.INFO, "Testing mobile view...");
        driver.manage().window().setSize(new org.openqa.selenium.Dimension(375, 812));
        Assert.assertTrue(driver.findElement(org.openqa.selenium.By.className("inventory_list")).isDisplayed(),
                "Product list should be visible on mobile view.");

        test.log(Status.INFO, "Testing desktop view...");
        driver.manage().window().maximize();
        Assert.assertTrue(driver.findElement(org.openqa.selenium.By.className("inventory_list")).isDisplayed(),
                "Product list should be visible on desktop view.");
    }

    @AfterClass
    public void tearDown() {
        DriverFactory.quitDriver();
    }

    private List<String> extractText(List<WebElement> elements) {
        List<String> texts = new ArrayList<>();
        for (WebElement e : elements) texts.add(e.getText().trim());
        return texts;
    }

    private List<Double> extractPrices(List<WebElement> elements) {
        List<Double> prices = new ArrayList<>();
        for (WebElement e : elements)
            prices.add(Double.parseDouble(e.getText().replace("$", "").trim()));
        return prices;
    }
}
