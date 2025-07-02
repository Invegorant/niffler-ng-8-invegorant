package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.page.component.SpendForm;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class AddSpendingPage extends BasePage<AddSpendingPage> {

    private static final String ADD_SPENDING_LABEL = "Add new spending";
    private final SelenideElement addButton = $("#save");

    private SpendForm spendForm = new SpendForm($("form.MuiGrid-container"));

    @Step("Verify 'Add spending' page is opened")
    @Nonnull
    public AddSpendingPage verifyPageIsOpened() {
        $("h2").shouldHave(text(ADD_SPENDING_LABEL));
        return this;
    }

    @Step("Fill all fields with spend data")
    @Nonnull
    public AddSpendingPage fillAllFields(SpendJson spend) {
        verifyPageIsOpened();
        spendForm
                .setAmount(spend.amount().toString())
                .setCurrency(spend.currency().name())
                .setCategory(spend.category().name())
                .setDate(String.valueOf(spend.spendDate()))
                .setDate(String.valueOf(spend.spendDate()))
                .setDescription(spend.description());
        return this;
    }

    @Step("Click confirm button")
    @Nonnull
    public MainPage clickConfirmButton() {
        addButton.click();
        return new MainPage();
    }
}
