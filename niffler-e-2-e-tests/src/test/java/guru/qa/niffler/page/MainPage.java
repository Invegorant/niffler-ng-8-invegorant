package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.components.Header;
import guru.qa.niffler.page.components.MenuComponent;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class MainPage {

    private final Header header = new Header();
    private final SelenideElement historyOfSpendingsHeader = $(byText("History of Spendings"));
    private final SelenideElement statisticsHeader = $(byText("Statistics"));
    private final ElementsCollection tableRows = $$("#spendings tbody tr");
    private final SelenideElement searchInput = $("input");

    public ProfilePage openProfilePage() {
        header.openMenu().selectMenuItem(MenuComponent.MenuItem.PROFILE);
        return new ProfilePage();
    }

    public FriendsPage openFriendsPage() {
        header.openMenu().selectMenuItem(MenuComponent.MenuItem.FRIENDS);
        return new FriendsPage();
    }

    public AllPeoplePage openAllPeoplePage() {
        header.openMenu().selectMenuItem(MenuComponent.MenuItem.ALL_PEOPLE);
        return new AllPeoplePage();
    }

    public EditSpendingPage editSpending(String spendingDescription) {
        searchInput.setValue(spendingDescription).pressEnter();
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

    public void checkThatTableContainsSpending(String spendingDescription) {
        searchInput.setValue(spendingDescription).pressEnter();
        tableRows.find(text(spendingDescription))
                .should(visible);
    }
}
