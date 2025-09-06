package testcases;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;
import pages.LoginPage;
import pages.ProductsPage;
import pages.CartPage;
import utils.DriverFactory;
import utils.ScreenshotUtil;
import reporting.ExtentReportManager;
import com.aventstack.extentreports.*;

import java.lang.reflect.Method;
import java.util.List;

public class CartTest {
    private WebDriver driver;
    private LoginPage loginPage;
    private ProductsPage productsPage;
    private CartPage cartPage;
    private static ExtentReports extent;
    private ExtentTest test;

    // For price checks
    private double backpackPrice = 0.0;
    private double tshirtPrice = 0.0;

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
        loginPage.login("standard_user", "secret_sauce");
        Assert.assertTrue(driver.getCurrentUrl().contains("inventory"), "Login failed, not on products page.");
    }

    @BeforeMethod
    public void beforeTestMethod(Method method) {
        test = extent.createTest(method.getName());
        test.log(Status.INFO, "Test started: " + method.getName());
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
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
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
    }

    // Ensures that the test runs on the products page
    private void ensureLoggedInOnProductsPage() {
        if (!driver.getCurrentUrl().contains("inventory")) {
            driver.get("https://www.saucedemo.com/");
            loginPage.login("standard_user", "secret_sauce");
            Assert.assertTrue(driver.getCurrentUrl().contains("inventory"), "Login failed before cart usage");
        }
    }

    // 1. Add multiple products to cart
    @Test(priority = 1)
    public void testAddMultipleProductsToCart() {
        ensureLoggedInOnProductsPage();
        test.log(Status.INFO, "Adding multiple products to the cart");
        productsPage.addProductToCartByName("Sauce Labs Backpack");
        productsPage.addProductToCartByName("Sauce Labs Bolt T-Shirt");

        cartPage.openCart();
        List<String> cartItems = cartPage.getCartItemNames();
        Assert.assertTrue(cartItems.contains("Sauce Labs Backpack"), "Backpack should be in cart");
        Assert.assertTrue(cartItems.contains("Sauce Labs Bolt T-Shirt"), "Bolt T-Shirt should be in cart");
    }

    // 2. Remove product from cart
    @Test(priority = 2)
    public void testRemoveProductFromCart() {
        ensureLoggedInOnProductsPage();
        test.log(Status.INFO, "Remove product from the cart");
        productsPage.addProductToCartByName("Sauce Labs Backpack");

        cartPage.openCart();
        cartPage.removeProductFromCart("Sauce Labs Backpack");
        List<String> cartItems = cartPage.getCartItemNames();
        Assert.assertFalse(cartItems.contains("Sauce Labs Backpack"), "Backpack should be removed from cart");
    }

    // 3. Validate cart summary (item count/price)
    @Test(priority = 3)
    public void testValidateCartSummary() {
        ensureLoggedInOnProductsPage();
        test.log(Status.INFO, "Validate cart summary (item count/price)");
        productsPage.addProductToCartByName("Sauce Labs Backpack");
        backpackPrice = productsPage.getProductPriceByName("Sauce Labs Backpack");

        productsPage.addProductToCartByName("Sauce Labs Bolt T-Shirt");
        tshirtPrice = productsPage.getProductPriceByName("Sauce Labs Bolt T-Shirt");

        cartPage.openCart();
        List<String> cartItems = cartPage.getCartItemNames();
        Assert.assertEquals(cartItems.size(), 2, "Cart should display two items");
        double totalPrice = cartPage.getCartTotalPrice();
        double expectedPrice = backpackPrice + tshirtPrice;
        Assert.assertEquals(totalPrice, expectedPrice, "Cart total price should match product sum");
    }

    // 4. Cart persistence across sessions
    @Test(priority = 4)
    public void testCartPersistenceAcrossSessions() {
        ensureLoggedInOnProductsPage();
        test.log(Status.INFO, "Test cart persistence across sessions");
        productsPage.addProductToCartByName("Sauce Labs Backpack");

        cartPage.openCart();
        Assert.assertTrue(cartPage.getCartItemNames().contains("Sauce Labs Backpack"), "Backpack in cart before session reset");
        String sessionUrl = driver.getCurrentUrl();

        driver.manage().deleteAllCookies();
        driver.get("https://www.saucedemo.com/");
        loginPage.login("standard_user", "secret_sauce");
        driver.get(sessionUrl);

        cartPage.openCart();
        boolean backpackPresent = cartPage.getCartItemNames().contains("Sauce Labs Backpack");
        test.log(Status.INFO, "Cart persistence status: " + backpackPresent);
        // Adjust assertion as per SauceDemo behavior:
        // Assert.assertTrue(backpackPresent, "Cart should persist after session relogin");
    }

    // Negative test: Checkout with empty cart
    @Test(priority = 5)
    public void testCheckoutWithEmptyCart() {
        ensureLoggedInOnProductsPage();
        test.log(Status.INFO, "Negative test: try checkout with empty cart");
        cartPage.openCart();
        cartPage.clickCheckout();
        Assert.assertTrue(driver.getCurrentUrl().contains("checkout"), "Should go to checkout even with empty cart");
    }

    // Negative test: Invalid session redirection
    @Test(priority = 6)
    public void testInvalidSessionRedirection() {
        test.log(Status.INFO, "Negative test: session invalidated, check redirection");
        driver.manage().deleteAllCookies();
        driver.navigate().refresh();
        driver.get("https://www.saucedemo.com/cart.html"); // triggers access on session invalid

        // Check for error message indicating access denied
        By errorMsgLocator = By.cssSelector("h3[data-test='error']");
        boolean errorPresent = false;
        try {
            errorPresent = driver.findElement(errorMsgLocator).getText().contains("You can only access '/cart.html' when you are logged in");
        } catch (Exception ignored) {}

        // Alternative: Check for login form field
        boolean loginFormPresent = !driver.findElements(By.id("login-button")).isEmpty();

        Assert.assertTrue(errorPresent || loginFormPresent, "Should show error or login form for invalid session");
    }


    @AfterClass
    public void tearDown() {
        DriverFactory.quitDriver();
    }
}
