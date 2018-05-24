package seng302;

import cucumber.api.CucumberOptions;
import cucumber.api.SnippetType;
import cucumber.api.junit.Cucumber;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import seng302.Utils.CommonTestMethods;

/**
 * Class for running Cucumber tests
 */

@RunWith(Cucumber.class)
@CucumberOptions(features = "features",
plugin = {
        "pretty",
        "html:target/site/cucumber-pretty",
        "json:target/cucumber.json"},
        snippets = SnippetType.CAMELCASE,
        glue = "seng302/steps"
)
public class GenericRunnerGUITest {
    @BeforeClass
    public static void initialization() {
        CommonTestMethods.runHeadless();
    }


}
