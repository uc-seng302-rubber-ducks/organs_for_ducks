package odms;

import cucumber.api.CucumberOptions;
import cucumber.api.SnippetType;
import cucumber.api.junit.Cucumber;
import odms.TestUtils.CommonTestMethods;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

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
        glue = "odms/steps"
)
public class GenericRunnerGUITest {
    @BeforeClass
    public static void initialization() {
        //CommonTestMethods.runHeadless();
    }


}
