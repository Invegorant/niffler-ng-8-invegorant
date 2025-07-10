package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.condition.SpendConditions;
import guru.qa.niffler.model.DataFilterValues;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.page.EditSpendingPage;
import guru.qa.niffler.page.MainPage;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@ParametersAreNonnullByDefault
public class SpendsTable extends BaseComponent<SpendsTable> {
    private static final Map<String, Integer> COLUMN_INDEX = Map.of(
            "select", 0,
            "category", 1,
            "description", 2,
            "amount", 3,
            "date", 4,
            "edit", 5
    );
    private final SelenideElement self = $("#spendings");
    private final ElementsCollection tableRows = $$("#spendings tbody tr");
    private final SelenideElement periodSelect = $("#period");
    private final SelenideElement deleteSpendButton = $("#delete");
    private final SearchField searchField = new SearchField();

    public SpendsTable(SelenideElement self) {
        super(self);
    }

    @Nonnull
    private ElementsCollection getCellsBySpendDescription(String spendDescription) {
        return tableRows
                .findBy(text(spendDescription))
                .$$("td");
    }

    @Step("Verify that the 'Add spending' table is displayed")
    @Nonnull
    public SpendsTable verifySpendTableDisplayed() {
        self.shouldBe(visible);
        return this;
    }

    @Step("Verify that 'Add spending' table contains all spends")
    public void verifySpendTableMatches(List<SpendJson> spends) {
        tableRows.should(SpendConditions.spend(spends.toArray(new SpendJson[0])));
    }

    @Step("Navigate to 'Spend edit' form by spend description: {spendDescription}")
    @Nonnull
    public EditSpendingPage editSpend(String spendDescription) {
        searchField.search(spendDescription);
        getCellsBySpendDescription(spendDescription)
                .get(COLUMN_INDEX.get("edit"))
                .click();
        return new EditSpendingPage();
    }

    @Step("Delete spend by description: {spendDescription}")
    @Nonnull
    public MainPage deleteSpend(String spendDescription) {
        getCellsBySpendDescription(spendDescription)
                .get(COLUMN_INDEX.get("select"))
                .click();
        deleteSpendButton.click();
        new AlertDialog().getDeleteButton().click();
        return new MainPage();
    }

    @Step("Verify that 'Add spending' table contains spend")
    @Nonnull
    public SpendsTable checkThatTableContains(SpendJson spend) {
        searchField.search(spend.description());
        tableRows
                .should(SpendConditions.spend(spend));
        return this;
    }

    @Step("Select period: {period}")
    @Nonnull
    public SpendsTable selectPeriod(DataFilterValues period) {
        periodSelect.click();
        $$("li").findBy(text(period.value)).click();
        return this;
    }
}
