package guru.qa.niffler.page.component;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Date;

import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class SpendForm extends BaseComponent<SpendForm> {

    private final SelenideElement amountInput = $("#amount");
    private final SelenideElement currencySelect = $("#currency");
    private final ElementsCollection currencyOptions = $("ul[role='listbox']").$$("li");
    private final SelenideElement categoryInput = $("#category");
    private final SelenideElement dateInput = $("input[name='date']");
    private final SelenideElement descriptionInput = $("#description");
    private final SelenideElement calendarButton = $("img[alt='Calendar']");

    private final Calendar calendar = new Calendar();

    public SpendForm(SelenideElement self) {
        super(self);
    }

    @Step("Set amount {amount}")
    @Nonnull
    public SpendForm setAmount(String amount) {
        amountInput.clear();
        amountInput.setValue(amount);
        return this;
    }

    @Step("Set currency {currency}")
    @Nonnull
    public SpendForm setCurrency(String currency) {
        currencySelect.click();
        currencyOptions.find(Condition.partialText(currency)).click();
        return this;
    }

    @Step("Set category {category}")
    @Nonnull
    public SpendForm setCategory(String category) {
        categoryInput.setValue(category);
        return this;
    }

    @Step("Set date {date} with date input")
    @Nonnull
    public SpendForm setDate(String date) {
        dateInput.setValue(date);
        return this;
    }

    @Step("Set date with calendar")
    @Nonnull
    public SpendForm setDescription(String description) {
        descriptionInput.clear();
        descriptionInput.setValue(description);
        return this;
    }

    @Step("Update date with calendar")
    @Nonnull
    public SpendForm updateDateWithCalendar(Date date) {
        calendarButton.click();
        calendar.setDate(date);
        return this;
    }
}
