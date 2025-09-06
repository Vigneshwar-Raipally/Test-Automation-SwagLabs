package stepDefinitions;

import io.cucumber.java.en.*;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import pages.LoginPage;
import utils.DriverFactory;

public class LoginSteps {
    private WebDriver driver;
    private LoginPage loginPage;

    @Given("User is on the SauceDemo login page")
    public void user_is_on_login_page() {
        driver = DriverFactory.getDriver();
        driver.get("https://www.saucedemo.com/");
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
    }

    // Additional Cucumber steps can go here for other login scenarios
}
