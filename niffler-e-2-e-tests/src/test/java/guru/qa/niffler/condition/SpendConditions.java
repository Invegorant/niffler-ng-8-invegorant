package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.WebElementsCondition;
import guru.qa.niffler.model.SpendJson;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;
import static com.codeborne.selenide.Selenide.$$;

@ParametersAreNonnullByDefault
public class SpendConditions {

    @Nonnull
    public static WebElementsCondition spend(@Nonnull SpendJson... expectedSpends) {
        return new WebElementsCondition() {

            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                ElementsCollection rows = $$(elements);

                if (ArrayUtils.isEmpty(expectedSpends)) {
                    throw new IllegalArgumentException("No expected spends given");
                }
                if (expectedSpends.length != elements.size()) {
                    final String message = String.format("List size mismatch (expected: %s, actual: %s)", expectedSpends.length, elements.size());
                    return rejected(message, elements);
                }

                for (int i = 0; i < rows.size(); i++) {
                    ElementsCollection cell = rows.get(i).$$("td");
                    if (!expectedSpends[i].category().name().equals(cell.get(1).getText())) {
                        String messageFail = "Spend category mismatch";
                        String message = "Spend category mismatch (expected: %s, actual: %s)"
                                .formatted(expectedSpends[i].category().name(), cell.get(1).getText());
                        return rejected(messageFail, message);
                    }
                    if (!(expectedSpends[i].amount().intValue() + " ₽").equals(cell.get(2).getText())) {
                        String messageFail = "Spend amount mismatch";
                        String message = "Spend amount mismatch (expected: %s, actual: %s)"
                                .formatted(expectedSpends[i].amount(), cell.get(2).getText());
                        return rejected(messageFail, message);
                    }
                    if (!expectedSpends[i].description().equals(cell.get(3).getText())) {
                        String messageFail = "Spend description mismatch";
                        String message = "Spend description mismatch (expected: %s, actual: %s)"
                                .formatted(expectedSpends[i].description(), cell.get(3).getText());
                        return rejected(messageFail, message);
                    }
                    if (!formatDate(expectedSpends[i].spendDate()).equals(cell.get(4).getText())) {
                        String messageFail = "Spend date mismatch";
                        String message = "Spend date mismatch (expected: %s, actual: %s)"
                                .formatted(formatDate(expectedSpends[i].spendDate()), cell.get(4).getText());
                        return rejected(messageFail, message);
                    }
                }
                return accepted();
            }

            @Override
            public String toString() {
                return "";
            }
        };
    }

    private static String formatDate(@Nonnull Date date) {
        final String DATE_FORMAT = "MMM d, yyyy";
        return new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH).format(date);
    }
}
