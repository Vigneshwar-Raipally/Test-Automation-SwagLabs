package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class CartPage {
    private WebDriver driver;

    // Locators
    private By cartIcon = By.className("shopping_cart_link");
    private By cartItemNames = By.className("inventory_item_name");
    private By removeButtons = By.xpath("//button[contains(text(),'Remove')]");
    private By checkoutButton = By.id("checkout");
    private By cartErrorMessage = By.cssSelector("h3[data-test='error']"); // adjust if SauceDemo gives alerts elsewhere
    private By cartItemContainer = By.className("cart_item");
    private By cartItemPrice = By.className("inventory_item_price");
    private By continueShoppingButton = By.id("continue-shopping");

    public CartPage(WebDriver driver) {
        this.driver = driver;
    }

    // Open the cart page from anywhere
    public void openCart() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement icon = wait.until(ExpectedConditions.elementToBeClickable(cartIcon));
        icon.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(cartItemContainer));
    }

    // Get all item names in the cart
    public List<String> getCartItemNames() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        List<WebElement> items = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(cartItemNames));
        return items.stream().map(WebElement::getText).collect(Collectors.toList());
    }

    // Remove item from cart by name
    public void removeProductFromCart(String productName) {
        List<WebElement> items = driver.findElements(cartItemContainer);
        for (WebElement item : items) {
            String name = item.findElement(cartItemNames).getText();
            if (name.trim().equals(productName.trim())) {
                item.findElement(By.xpath(".//button[contains(text(),'Remove')]")).click();
                break;
            }
        }
    }

    // Click checkout in cart
    public void clickCheckout() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement btn = wait.until(ExpectedConditions.visibilityOfElementLocated(checkoutButton));
        btn.click();
    }

    // Get any cart error message (for failed/empty checkout)
    public String getCartErrorMessage() {
        // Wait for error message (if present)
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(cartErrorMessage)).getText();
        } catch (Exception e) {
            return ""; // No error
        }
    }

    // Get total price of items currently in the cart
    public double getCartTotalPrice() {
        List<WebElement> items = driver.findElements(cartItemPrice);
        double total = 0.0;
        for (WebElement item : items) {
            try {
                total += Double.parseDouble(item.getText().replace("$", "").trim());
            } catch (Exception ignored) {}
        }
        return total;
    }
}
