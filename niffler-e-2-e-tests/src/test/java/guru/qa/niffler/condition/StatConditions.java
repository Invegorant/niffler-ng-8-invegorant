package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.WebElementsCondition;
import guru.qa.niffler.model.Bubble;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.stream.Collectors;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;

@ParametersAreNonnullByDefault
public class StatConditions {

    @Nonnull
    public static WebElementCondition color(Color expectedColor) {
        return new WebElementCondition("color " + expectedColor.rgb) {
            @NotNull
            @Override
            public CheckResult check(Driver driver, WebElement element) {
                final String rgba = element.getCssValue("background-color");
                return new CheckResult(
                        expectedColor.rgb.equals(rgba),
                        rgba
                );
            }
        };
    }

    @Nonnull
    public static WebElementsCondition color(@Nonnull Color... expectedColors) {
        return new WebElementsCondition() {

            private final String expectedRgba = Arrays.stream(expectedColors).map(c -> c.rgb).toList().toString();

            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                if (ArrayUtils.isEmpty(expectedColors)) {
                    throw new IllegalArgumentException("No expected colors given");
                }
                if (expectedColors.length != elements.size()) {
                    final String message = String.format("List size mismatch (expected: %s, actual: %s)", expectedColors.length, elements.size());
                    return rejected(message, elements);
                }

                boolean passed = true;
                final List<String> actualRgbaList = new ArrayList<>();
                for (int i = 0; i < elements.size(); i++) {
                    final WebElement elementToCheck = elements.get(i);
                    final Color colorToCheck = expectedColors[i];
                    final String rgba = elementToCheck.getCssValue("background-color");
                    actualRgbaList.add(rgba);
                    if (passed) {
                        passed = colorToCheck.rgb.equals(rgba);
                    }
                }

                if (!passed) {
                    final String actualRgba = actualRgbaList.toString();
                    final String message = String.format(
                            "List colors mismatch (expected: %s, actual: %s)", expectedRgba, actualRgba
                    );
                    return rejected(message, actualRgba);
                }
                return accepted();
            }

            @Override
            public String toString() {
                return expectedRgba;
            }
        };
    }

    @Nonnull
    public static WebElementsCondition bubble(@Nonnull Bubble... expectedBubbles) {
        return new WebElementsCondition() {

            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                if (ArrayUtils.isEmpty(expectedBubbles)) {
                    throw new IllegalArgumentException("No expected bubbles given");
                }
                if (expectedBubbles.length != elements.size()) {
                    final String message = String.format("List size mismatch (expected: %s, actual: %s)", expectedBubbles.length, elements.size());
                    return rejected(message, elements);
                }

                boolean passed = true;
                ArrayList<String> actualBubbleColor = new ArrayList<>();
                ArrayList<String> actualBubbleText = new ArrayList<>();
                for (int i = 0; i < elements.size(); i++) {
                    final WebElement elementToCheck = elements.get(i);
                    final Bubble bubbleToCheck = expectedBubbles[i];
                    final String rgba = elementToCheck.getCssValue("background-color");
                    final String text = elementToCheck.getText();
                    actualBubbleColor.add(rgba);
                    actualBubbleText.add(text);
                    if (passed) {
                        passed = (bubbleToCheck.color().rgb.equals(rgba) && bubbleToCheck.text().equals(text));
                    }
                }

                if (!passed) {
                    final String message = "Bubbles mismatch";
                    StringBuilder actualValuesMessage = new StringBuilder();
                    for (int i = 0; i < actualBubbleColor.size(); i++) {
                        actualValuesMessage.append(("Bubble: {color = %s, text = %s}, ").formatted(actualBubbleColor.get(i), actualBubbleText.get(i)));
                    }
                    return rejected(message, actualValuesMessage);
                }
                return accepted();
            }

            @Override
            public String toString() {
                return Arrays.toString(expectedBubbles);
            }
        };
    }

    public static WebElementsCondition bubblesInAnyOrder(@Nonnull Bubble... expectedBubbles) {
        return new WebElementsCondition() {
            private final Set<String> expectedTexts = Arrays.stream(expectedBubbles)
                    .map(Bubble::text)
                    .collect(Collectors.toSet());

            @Nonnull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {

                if (ArrayUtils.isEmpty(expectedBubbles)) {
                    throw new IllegalArgumentException("No expected bubbles given");
                }

                if (expectedBubbles.length != elements.size()) {
                    String message = String.format("List size mismatch (expected: %s, actual: %s)",
                            expectedBubbles.length, elements.size());
                    return rejected(message, elements);
                }

                boolean passed = true;
                HashMap<String, String> actualBubblesMap = actualBubblesMap(elements);

                for (Bubble bubble : expectedBubbles) {
                    if (passed) {
                        passed = actualBubblesMap.values().containsAll(expectedTexts) &&
                                actualBubblesMap.get(bubble.color().rgb).equals(bubble.text());
                    }
                }

                if (!passed) {
                    String message = "Bubbles mismatch";
                    StringBuilder actualValuesMessage = new StringBuilder();
                    for (Map.Entry<String, String> entry : actualBubblesMap.entrySet()) {
                        actualValuesMessage.append(("Bubble: {color = %s, text = %s}, ").formatted(entry.getKey(), entry.getValue()));
                    }
                    return rejected(message, actualValuesMessage);
                }
                return accepted();
            }

            @Override
            public String toString() {
                return Arrays.toString(expectedBubbles);
            }
        };
    }

    public static WebElementsCondition bubblesContains(@Nonnull Bubble... expectedBubbles) {
        return new WebElementsCondition() {

            @Nonnull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {

                if (ArrayUtils.isEmpty(expectedBubbles)) {
                    throw new IllegalArgumentException("No expected bubbles given");
                }

                if (expectedBubbles.length > elements.size()) {
                    String message = String.format("List size is more than expected (expected: %s, actual: %s)",
                            expectedBubbles.length, elements.size());
                    return rejected(message, elements);
                }
                boolean passed = true;
                HashMap<String, String> actualBubblesMap = actualBubblesMap(elements);

                for (Bubble bubble : expectedBubbles) {
                    if (passed) {
                        passed = actualBubblesMap.containsValue(bubble.text()) &&
                                actualBubblesMap.get(bubble.color().rgb).equals(bubble.text());
                    }
                }

                if (!passed) {
                    String message = "Bubbles mismatch";
                    StringBuilder actualValuesMessage = new StringBuilder();
                    for (Map.Entry<String, String> entry : actualBubblesMap.entrySet()) {
                        actualValuesMessage.append(("Bubble: {color = %s, text = %s}, ").formatted(entry.getKey(), entry.getValue()));
                    }
                    return rejected(message, actualValuesMessage);
                }
                return accepted();
            }

            @Override
            public String toString() {
                return Arrays.toString(expectedBubbles);
            }
        };
    }

    private static HashMap<String, String> actualBubblesMap(List<WebElement> elements) {
        HashMap<String, String> actualBubblesMap = new HashMap<>();

        for (WebElement elementToCheck : elements) {
            final String rgba = elementToCheck.getCssValue("background-color");
            final String text = elementToCheck.getText();
            actualBubblesMap.put(rgba, text);
        }
        return actualBubblesMap;
    }
}
