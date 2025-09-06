package utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenshotUtil {

    private static final String SCREENSHOT_DIR = "reports/screenshots/";

    /**
     * Captures screenshot and saves it in reports/screenshots folder.
     * @param driver WebDriver instance
     * @param stepName name of the step (used in filename)
     * @return absolute path of the screenshot (useful for ExtentReports)
     */
    public static String captureScreenshot(WebDriver driver, String stepName) {
        // Create screenshot folder if it doesn't exist
        File directory = new File(SCREENSHOT_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Timestamp to avoid overwriting
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        // Screenshot file
        File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        String screenshotPath = SCREENSHOT_DIR + stepName + "_" + timestamp + ".png";
        File destFile = new File(screenshotPath);

        try {
            Files.copy(srcFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return destFile.getAbsolutePath(); // useful for reporting
    }
}
