package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class EditSpendingPage extends BasePage<EditSpendingPage> {

    private final SelenideElement descriptionInput = $("#description");
    private final SelenideElement amountInput = $("#amount");
    private final SelenideElement submitBtn = $("#save");

    public void editDescription(String description) {
        descriptionInput.clear();
        descriptionInput.setValue(description);
        submitBtn.click();
    }

    @Nonnull
    public EditSpendingPage setNewSpendingAmount(double amount) {
        amountInput.clear();
        amountInput.setValue(String.valueOf(amount));
        return this;
    }

    @Nonnull
    public EditSpendingPage saveSpending() {
        submitBtn.click();
        return this;
    }
}
