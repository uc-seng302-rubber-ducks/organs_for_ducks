package seng302.steps;

import java.util.HashMap;
import java.util.Map;

public class TestContext {
    private static Map<String, Map<String, Object>> contexts;

    private TestContext() {
        contexts = new HashMap<>();
    }

    private static void instantiate() {
        if (contexts == null) {
            contexts = new HashMap<>();
        }
    }

    public static Map<String, Object> getScenario(String scenario) {
        instantiate();
        if (!contexts.containsKey(scenario)) {
            contexts.put(scenario, new HashMap<>());
        }

        return contexts.get(scenario);

    }

}
