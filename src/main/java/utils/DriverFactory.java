package utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

public class DriverFactory {

    private static WebDriver driver;

    private static final String CHROME_DRIVER_PATH = "C:\\Users\\datta\\Downloads\\chromedriver-win64\\chromedriver.exe";
    private static final String EDGE_DRIVER_PATH   = "C:\\Users\\datta\\Downloads\\edgedriver_win64\\msedgedriver.exe";

    public static WebDriver getDriver() {
        if (driver == null) {
            initDriver("chrome");  // default browser
        }
        return driver;
    }

    public static void initDriver(String browser) {
        if (driver != null) return;

        boolean isJenkins = System.getenv("JENKINS_HOME") != null;

        switch (browser.toLowerCase()) {
            case "edge":
                System.setProperty("webdriver.edge.driver", EDGE_DRIVER_PATH);
                EdgeOptions edgeOptions = new EdgeOptions();
                if (isJenkins) {
                    edgeOptions.addArguments("--headless=new");
                    edgeOptions.addArguments("--disable-gpu");
                }
                edgeOptions.addArguments("--remote-allow-origins=*");
                driver = new EdgeDriver(edgeOptions);
                break;

            case "chrome":
            default:
                System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_PATH);
                ChromeOptions chromeOptions = new ChromeOptions();
                // Anti-detection + stability
                chromeOptions.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
                chromeOptions.setExperimentalOption("useAutomationExtension", false);
                chromeOptions.addArguments("--disable-blink-features=AutomationControlled");
                if (isJenkins) {
                    chromeOptions.addArguments("--headless=new");
                    chromeOptions.addArguments("--disable-gpu");
                }
                chromeOptions.addArguments("--remote-allow-origins=*");
                driver = new ChromeDriver(chromeOptions);
                break;
        }

        driver.manage().window().maximize();
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
            driver.quit();
            driver = null;
        }
    }
}
