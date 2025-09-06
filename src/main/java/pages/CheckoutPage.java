package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class CheckoutPage {
    private WebDriver driver;

    // Locators for Step One
    private By firstNameField = By.id("first-name");
    private By lastNameField = By.id("last-name");
    private By postalCodeField = By.id("postal-code");
    private By continueButton = By.id("continue");
    private By cancelStepOneButton = By.id("cancel");
    private By errorMsg = By.cssSelector("h3[data-test='error']");

    // Locators for Step Two
    private By summaryItemTotal = By.className("summary_subtotal_label");
    private By summaryTax = By.className("summary_tax_label");
    private By summaryTotal = By.className("summary_total_label");
    private By finishButton = By.id("finish");
    private By cancelStepTwoButton = By.id("cancel");
    private By confirmationHeader = By.className("complete-header");
    private By confirmationMessage = By.className("complete-text");

    public CheckoutPage(WebDriver driver) {
        this.driver = driver;
    }

    // Fill checkout information
    public void enterCheckoutInfo(String firstName, String lastName, String postalCode) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(firstNameField)).clear();
        driver.findElement(firstNameField).sendKeys(firstName);
        driver.findElement(lastNameField).clear();
        driver.findElement(lastNameField).sendKeys(lastName);
        driver.findElement(postalCodeField).clear();
        driver.findElement(postalCodeField).sendKeys(postalCode);
    }

    // Continue at Step One
    public void clickContinue() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.elementToBeClickable(continueButton)).click();
    }

    // Finish checkout
    public void finishCheckout() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.elementToBeClickable(finishButton)).click();
    }

    // Cancel Step One
    public void clickCancelStepOne() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.elementToBeClickable(cancelStepOneButton)).click();
    }

    // Cancel Step Two
    public void clickCancelStepTwo() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.elementToBeClickable(cancelStepTwoButton)).click();
    }

    // Get error message in Step One
    public String getCheckoutErrorMessage() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
            return wait.until(ExpectedConditions.visibilityOfElementLocated(errorMsg)).getText();
        } catch (Exception e) {
            return "";
        }
    }

    // Order complete (confirmation page) checks
    public boolean isOrderComplete() {
        return !driver.findElements(confirmationHeader).isEmpty();
    }

    public String getOrderConfirmationMessage() {
        try {
            return driver.findElement(confirmationMessage).getText();
        } catch (Exception e) {
            return "";
        }
    }

    // Order summary info (Step Two)
    public double getSummaryItemTotal() {
        String labelText = driver.findElement(summaryItemTotal).getText(); // "Item total: $29.99"
        return Double.parseDouble(labelText.split("\\$")[1].trim());
    }

    public double getSummaryTax() {
        String labelText = driver.findElement(summaryTax).getText(); // "Tax: $2.40"
        return Double.parseDouble(labelText.split("\\$")[1].trim());
    }

    public double getSummaryTotal() {
        String labelText = driver.findElement(summaryTotal).getText(); // "Total: $32.39"
        return Double.parseDouble(labelText.split("\\$")[1].trim());
    }
}
