package reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentReportManager {

    private static ExtentReports extent;

    public static ExtentReports getReporter() {
        if (extent == null) {
            String reportPath = System.getProperty("user.dir") + "/reports/extent-reports/ExecutionReport.html";
            ExtentSparkReporter reporter = new ExtentSparkReporter(reportPath);
            reporter.config().setReportName("SauceDemo Automation Report");
            reporter.config().setDocumentTitle("Automation Execution Report");

            extent = new ExtentReports();
            extent.attachReporter(reporter);
            extent.setSystemInfo("Project", "SauceDemo Automation");
            extent.setSystemInfo("Tester", "Capstone QA");
            extent.setSystemInfo("Browser", "Chrome/Edge");
        }
        return extent;
    }
}
