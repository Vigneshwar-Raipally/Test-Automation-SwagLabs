package base;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.testng.ITestResult;
import org.testng.annotations.*;
import utils.ConfigReader;
import utils.DriverFactory;
import utils.ScreenshotUtil;

public class BaseTest {

	protected static ExtentReports extent;
	protected static ExtentTest test;

	@BeforeSuite
	public void setUpReport() {
		String reportPath = System.getProperty("user.dir") + "/reports/extent-reports/AutomationReport.html";
		ExtentSparkReporter reporter = new ExtentSparkReporter(reportPath);
		reporter.config().setDocumentTitle("Swag Labs Automation Report");
		reporter.config().setReportName("Functional Test Results");

		extent = new ExtentReports();
		extent.attachReporter(reporter);
		extent.setSystemInfo("Project", "Test-Automation-SwagLabs");
		extent.setSystemInfo("Tester", "Vignesh");
	}

	@BeforeMethod
	@Parameters({ "browser" })
	public void setUp(@Optional("chrome") String browser) {
		// init driver
		DriverFactory.initDriver(browser);

		// read URL from config.properties
		String url = ConfigReader.getProperty("app.url");
		DriverFactory.openUrl(url);

		// start Extent test node
		test = extent.createTest(getClass().getSimpleName());
	}

	@AfterMethod
	public void tearDown(ITestResult result) {
		try {
			String screenshotPath = ScreenshotUtil.captureScreenshot(DriverFactory.getDriver(), result.getName());
			if (result.getStatus() == ITestResult.FAILURE) {
				test.fail("Test Failed: " + result.getThrowable()).addScreenCaptureFromPath(screenshotPath);
			} else if (result.getStatus() == ITestResult.SUCCESS) {
				test.pass("Test Passed").addScreenCaptureFromPath(screenshotPath);
			} else {
				test.skip("Test Skipped: " + result.getThrowable());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// DriverFactory.quitDriver();
		}
	}

	// Reusable pause utility
	protected void pause(int seconds) {
		try {
			Thread.sleep(seconds * 1000L);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	@AfterSuite
	public void tearDownReport() {
		if (extent != null) {
			extent.flush();
		}
	}
}
