package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.components.Header;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class MainPage extends AbstractPage {

    private final Header header = new Header();
    private final SelenideElement historyOfSpendingsHeader = $(byText("History of Spendings"));
    private final SelenideElement statisticsHeader = $(byText("Statistics"));
    private final ElementsCollection tableRows = $$("#spendings tbody tr");

    public EditSpendingPage editSpending(String spendingDescription) {
        tableRows.find(text(spendingDescription))
                .$$("td")
                .get(5)
                .click();
        return new EditSpendingPage();
    }

    public void checkHistoryOfSpendingIsPresent() {
        historyOfSpendingsHeader.shouldBe(visible);
    }

    public MainPage checkStatisticsIsPresent() {
        statisticsHeader.shouldBe(visible);
        return this;
    }

    public void checkThatTableContains(String spendingDescription) {
        tableRows.find(text(spendingDescription))
                .should(visible);
    }
}
