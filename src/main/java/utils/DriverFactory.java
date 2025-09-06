package utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;

public class DriverFactory {

    private static WebDriver driver;

    private static final String CHROME_DRIVER_PATH = "C:\\Users\\datta\\Downloads\\chromedriver-win64\\chromedriver.exe";
    private static final String EDGE_DRIVER_PATH   = "C:\\Users\\datta\\Downloads\\edgedriver_win64\\msedgedriver.exe";

    public static WebDriver getDriver() {
        if (driver == null) {
            initDriver("chrome"); // default
        }
        return driver;
    }

    public static void initDriver(String browser) {
        if (driver != null) return;

        switch (browser.toLowerCase()) {
            case "edge":
                System.setProperty("webdriver.edge.driver", EDGE_DRIVER_PATH);
                driver = new EdgeDriver();
                break;

            case "chrome":
            default:
                System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_PATH);
                ChromeOptions options = new ChromeOptions();
                // Recommended anti-detection options for SauceDemo/login session stability:
                options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
                options.setExperimentalOption("useAutomationExtension", false);
                options.addArguments("--disable-blink-features=AutomationControlled");
                driver = new ChromeDriver(options);
                break;
        }
        driver.manage().window().maximize();
        // Clear cookies after starting browser to ensure a fresh session
        driver.manage().deleteAllCookies();
    }

    public static void setBrowser(String browser) {
        quitDriver();          // close existing driver if running
        initDriver(browser);   // start new driver with requested browser
    }

    public static void openUrl(String url) {
        getDriver().get(url);
    }

    public static void quitDriver() {
        if (driver != null) {
            driver.quit(); // uncomment to actually quit browser
            driver = null;
        }
    }
}
