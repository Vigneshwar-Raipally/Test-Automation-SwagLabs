package stepDefinitions;

import io.cucumber.java.en.*;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import pages.LoginPage;
import utils.DriverFactory;

public class LoginSteps {

    private WebDriver driver;
    private LoginPage loginPage;

    // Default browser for single login scenario
    @Given("User opens the SauceDemo login page")
    public void user_opens_login_page() {
        DriverFactory.setBrowser("chrome"); // default browser
        driver = DriverFactory.getDriver();
        DriverFactory.openUrl("https://www.saucedemo.com/");
        loginPage = new LoginPage(driver);
    }

    // Multi-browser scenario
    @Given("User opens the SauceDemo login page on browser {string}")
    public void user_opens_login_page_on_browser(String browser) {
        DriverFactory.setBrowser(browser);
        driver = DriverFactory.getDriver();
        DriverFactory.openUrl("https://www.saucedemo.com/");
        loginPage = new LoginPage(driver);
    }

    @When("User enters valid username {string} and password {string}")
    public void user_enters_valid_credentials(String username, String password) {
        loginPage.login(username, password);
    }

    @Then("User should be redirected to the products page")
    public void user_on_products_page() {
        Assert.assertTrue(driver.getCurrentUrl().contains("inventory"),
                "User is not on the products page after login.");
        DriverFactory.quitDriver();
    }
}
