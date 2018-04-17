package seng302;

import cucumber.api.CucumberOptions;
import cucumber.api.SnippetType;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = "features",
format = {
        "pretty",
        "html:target/site/cucumber-pretty",
        "json:target/cucumber.json"},
        snippets = SnippetType.CAMELCASE,
        glue = "steps"
)
public class GenericRunnerTest {

}
