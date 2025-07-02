package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class AlertDialog extends BaseComponent<AlertDialog> {

    public AlertDialog() {
        super($("div[role='dialog']"));
    }

    public AlertDialog(SelenideElement self) {
        super(self);
    }

    public SelenideElement getDeleteButton() {
        return self.$$("button").findBy(text("Delete"));
    }

    public SelenideElement getDeclineButton() {
        return self.$$("button").findBy(text("Decline"));
    }
}
