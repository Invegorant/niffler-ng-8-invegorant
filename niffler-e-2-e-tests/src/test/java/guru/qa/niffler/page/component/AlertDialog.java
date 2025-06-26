package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class AlertDialog {
    private final SelenideElement self = $("div[role='dialog']");

    public SelenideElement getDeleteButton() {
        return self.$$("button").findBy(text("Delete"));
    }

    public SelenideElement getDeclineButton() {
        return self.$$("button").findBy(text("Decline"));
    }
}
