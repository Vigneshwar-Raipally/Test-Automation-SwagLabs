package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
import java.util.List;

public class ProductsPage {
    private WebDriver driver;

    // Locators
    private By sortDropdown = By.className("product_sort_container");
    private By productNames = By.className("inventory_item_name");
    private By productPrices = By.className("inventory_item_price");
    private By productImages = By.cssSelector(".inventory_item_img img");
    private By menuButton = By.id("react-burger-menu-btn");
    private By aboutLink = By.id("about_sidebar_link");
    private By logoutLink = By.id("logout_sidebar_link");
    private By resetAppLink = By.id("reset_sidebar_link");
    private By inventoryItem = By.className("inventory_item");

    public ProductsPage(WebDriver driver) {
        this.driver = driver;
    }

    // Sorting actions
    public void selectSortOption(String visibleText) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement dropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(sortDropdown));
        Select select = new Select(dropdown);
        select.selectByVisibleText(visibleText);
    }

    // Product details
    public List<WebElement> getProductNames() {
        return driver.findElements(productNames);
    }
    public List<WebElement> getProductPrices() {
        return driver.findElements(productPrices);
    }
    public List<WebElement> getProductImages() {
        return driver.findElements(productImages);
    }

    // Add product to cart by its name
    public void addProductToCartByName(String name) {
        List<WebElement> products = driver.findElements(inventoryItem);
        for (WebElement product : products) {
            String productName = product.findElement(productNames).getText().trim();
            if (productName.equals(name)) {
                // Find and click the "Add to cart" button within this product
                // Only click if it's not already "Remove"
                WebElement addToCartButton = product.findElement(By.xpath(".//button"));
                String btnText = addToCartButton.getText().trim();
                if (btnText.equalsIgnoreCase("Add to cart")) {
                    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
                    wait.until(ExpectedConditions.elementToBeClickable(addToCartButton)).click();
                }
                break;
            }
        }
    }

    // Get the price of a product by its name
    public double getProductPriceByName(String name) {
        List<WebElement> products = driver.findElements(inventoryItem);
        for (WebElement product : products) {
            String productName = product.findElement(productNames).getText().trim();
            if (productName.equals(name)) {
                String priceText = product.findElement(productPrices).getText().replace("$", "").trim();
                return Double.parseDouble(priceText);
            }
        }
        throw new RuntimeException("Product \"" + name + "\" not found.");
    }

    // Sidebar actions with robust waits
    public void openSidebar() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement menu = wait.until(ExpectedConditions.elementToBeClickable(menuButton));
        menu.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(aboutLink));
        wait.until(ExpectedConditions.elementToBeClickable(aboutLink));
    }
    public void clickAbout() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement about = wait.until(ExpectedConditions.elementToBeClickable(aboutLink));
        about.click();
    }
    public void clickLogout() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement logout = wait.until(ExpectedConditions.elementToBeClickable(logoutLink));
        logout.click();
    }
    public void clickResetAppState() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement reset = wait.until(ExpectedConditions.elementToBeClickable(resetAppLink));
        reset.click();
    }
}
