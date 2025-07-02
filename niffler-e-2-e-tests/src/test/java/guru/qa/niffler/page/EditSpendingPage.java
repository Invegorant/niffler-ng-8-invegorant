package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.SpendForm;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Date;

import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class EditSpendingPage extends BasePage<EditSpendingPage> {

    private final SpendForm spendForm = new SpendForm($("form.MuiGrid-container"));
    private final SelenideElement saveButton = $("#save");

    @Step("Edit spending description: {description}")
    @Nonnull
    public EditSpendingPage editDescription(String description) {
        spendForm.setDescription(description);
        saveButton.click();
        return this;
    }

    @Step("Edit spending amount: {amount}")
    @Nonnull
    public EditSpendingPage editAmount(String amount) {
        spendForm.setAmount(amount);
        saveButton.click();
        return this;
    }

    @Step("Edit spending date: {date}")
    @Nonnull
    public EditSpendingPage editDateWithCalendar(Date date) {
        spendForm.updateDateWithCalendar(date);
        saveButton.click();
        return this;
    }
}
