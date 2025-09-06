package testcases;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;
import pages.LoginPage;
import pages.ProductsPage;
import pages.CartPage;
import pages.CheckoutPage;
import utils.DriverFactory;
import utils.ScreenshotUtil;
import reporting.ExtentReportManager;
import com.aventstack.extentreports.*;

import java.lang.reflect.Method;
import java.util.List;

public class CheckoutTest {
    private WebDriver driver;
    private LoginPage loginPage;
    private ProductsPage productsPage;
    private CartPage cartPage;
    private CheckoutPage checkoutPage;
    private static ExtentReports extent;
    private ExtentTest test;

    @BeforeSuite
    public void beforeSuite() {
        extent = ExtentReportManager.getReporter();
    }

    @BeforeClass
    public void setUp() {
        DriverFactory.setBrowser("edge");
        driver = DriverFactory.getDriver();
        driver.manage().deleteAllCookies();
        driver.get("https://www.saucedemo.com/");
        loginPage = new LoginPage(driver);
        productsPage = new ProductsPage(driver);
        cartPage = new CartPage(driver);
        checkoutPage = new CheckoutPage(driver);
        loginPage.login("standard_user", "secret_sauce");
        Assert.assertTrue(driver.getCurrentUrl().contains("inventory"), "Login failed, not on products page.");
    }

    @BeforeMethod
    public void beforeTestMethod(Method method) {
        test = extent.createTest(method.getName());
        test.log(Status.INFO, "Test started: " + method.getName());
        try { Thread.sleep(800); } catch (InterruptedException ignored) {}
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
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
    }

    // Helper to start from products, login, add item
    private void prepareForCheckout() {
        if (!driver.getCurrentUrl().contains("inventory")) {
            driver.get("https://www.saucedemo.com/");
            loginPage.login("standard_user", "secret_sauce");
            Assert.assertTrue(driver.getCurrentUrl().contains("inventory"), "Login failed before checkout test");
        }
        productsPage.addProductToCartByName("Sauce Labs Backpack");
        cartPage.openCart();
        cartPage.clickCheckout();
        Assert.assertTrue(driver.getCurrentUrl().contains("checkout-step-one"), "Should open checkout step one");
    }

    @Test(priority = 1)
    public void testCompleteCheckoutWithValidData() {
        prepareForCheckout();
        test.log(Status.INFO, "Complete checkout with valid data");
        checkoutPage.enterCheckoutInfo("John", "Doe", "12345");
        checkoutPage.clickContinue();
        checkoutPage.finishCheckout();
        Assert.assertTrue(checkoutPage.isOrderComplete(), "Order confirmation should be visible after valid checkout");
    }

    @Test(priority = 2)
    public void testCompleteCheckoutWithValidDataRepeat() {
        prepareForCheckout();
        test.log(Status.INFO, "Repeat: Complete checkout with valid data (different user)");
        checkoutPage.enterCheckoutInfo("Jane", "Smith", "67890");
        checkoutPage.clickContinue();
        checkoutPage.finishCheckout();
        Assert.assertTrue(checkoutPage.isOrderComplete(), "Order confirmation should be visible after valid checkout");
    }

    @Test(priority = 3)
    public void testCheckoutWithEmptyFields() {
        prepareForCheckout();
        test.log(Status.INFO, "Checkout with empty fields");
        checkoutPage.enterCheckoutInfo("", "", "");
        checkoutPage.clickContinue();
        String errorMsg = checkoutPage.getCheckoutErrorMessage();
        Assert.assertTrue(errorMsg != null && !errorMsg.isEmpty(),
                "Error message should appear for missing fields");
    }

    @Test(priority = 4)
    public void testCheckoutWithInvalidPostalCode() {
        prepareForCheckout();
        test.log(Status.INFO, "Checkout with invalid postal code");
        checkoutPage.enterCheckoutInfo("Tom", "Jerry", "@@@##");
        checkoutPage.clickContinue();
        // SauceDemo allows this! Verify navigation to next step instead of error.
        Assert.assertTrue(driver.getCurrentUrl().contains("checkout-step-two"),
                "Should go to checkout summary even with invalid postal code");
    }

    @Test(priority = 5)
    public void testCancelCheckoutSteps() {
        prepareForCheckout();
        test.log(Status.INFO, "Cancel checkout at Step One");
        checkoutPage.clickCancelStepOne();
        Assert.assertTrue(driver.getCurrentUrl().contains("cart"), "Cancel Step One should return to cart");

        cartPage.clickCheckout();
        checkoutPage.enterCheckoutInfo("Ron", "Weasley", "11223");
        checkoutPage.clickContinue();
        test.log(Status.INFO, "Cancel checkout at Step Two");
        checkoutPage.clickCancelStepTwo();
        Assert.assertTrue(driver.getCurrentUrl().contains("inventory"), "Cancel Step Two should return to products");
    }

    @Test(priority = 6)
    public void testOrderSummaryValidation() {
        // Get the price BEFORE going to checkout
        if (!driver.getCurrentUrl().contains("inventory")) {
            driver.get("https://www.saucedemo.com/");
            loginPage.login("standard_user", "secret_sauce");
            Assert.assertTrue(driver.getCurrentUrl().contains("inventory"), "Login failed before order summary");
        }
        productsPage.addProductToCartByName("Sauce Labs Backpack");
        double itemPrice = productsPage.getProductPriceByName("Sauce Labs Backpack");

        cartPage.openCart();
        cartPage.clickCheckout();
        checkoutPage.enterCheckoutInfo("Order", "Test", "55555");
        checkoutPage.clickContinue();

        double summaryItemTotal = checkoutPage.getSummaryItemTotal();
        double summaryTax = checkoutPage.getSummaryTax();
        double summaryTotal = checkoutPage.getSummaryTotal();

        Assert.assertEquals(summaryItemTotal, itemPrice, "Item total should match product price");
        Assert.assertTrue(summaryTax > 0, "Tax should be present");
        Assert.assertEquals(summaryTotal, summaryItemTotal + summaryTax, "Total should be sum of item total and tax");
        checkoutPage.finishCheckout();
        Assert.assertTrue(checkoutPage.isOrderComplete(), "Order confirmation should be visible after summary checked");
    }

    @Test(priority = 7)
    public void testOrderConfirmationPageValidation() {
        prepareForCheckout();
        test.log(Status.INFO, "Order confirmation validation");
        checkoutPage.enterCheckoutInfo("Final", "Confirm", "22222");
        checkoutPage.clickContinue();
        checkoutPage.finishCheckout();
        Assert.assertTrue(checkoutPage.isOrderComplete(), "Order confirmation page should display complete status");
        String confirmationMsg = checkoutPage.getOrderConfirmationMessage();
        Assert.assertTrue(confirmationMsg != null && !confirmationMsg.isEmpty(), "Confirmation message should be shown");
    }

    @AfterClass
    public void tearDown() {
        DriverFactory.quitDriver();
    }
}
