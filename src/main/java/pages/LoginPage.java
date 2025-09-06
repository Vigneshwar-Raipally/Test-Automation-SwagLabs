package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {

	private WebDriver driver;

	// Locators
	private By username = By.id("user-name");
	private By password = By.id("password");
	private By loginButton = By.id("login-button");
	private By errorMessage = By.cssSelector("h3[data-test='error']");

	// Constructor
	public LoginPage(WebDriver driver) {
		this.driver = driver;
	}

	// Actions
	public void enterUsername(String user) {
		driver.findElement(username).clear();
		driver.findElement(username).sendKeys(user);
	}

	public void enterPassword(String pass) {
		driver.findElement(password).clear();
		driver.findElement(password).sendKeys(pass);
	}

	public void clickLogin() {
		driver.findElement(loginButton).click();
	}

	public String getErrorMessage() {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		WebElement errorElement = wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage));
		return errorElement.getText();
	}

	public void login(String user, String pass) {
		enterUsername(user);
		enterPassword(pass);
		clickLogin();
	}

}
