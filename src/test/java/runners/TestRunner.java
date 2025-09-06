package runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
    features = "src/test/resources/features",
    glue = "stepDefinitions", // Package for step definitions
    plugin = {
        "pretty",
        "html:reports/cucumber-reports/cucumber-report.html",
        "json:reports/cucumber-reports/cucumber-report.json"
    },
    monochrome = true
)
public class TestRunner extends AbstractTestNGCucumberTests {}
