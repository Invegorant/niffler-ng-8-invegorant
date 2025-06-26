package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.AlertDialog;
import guru.qa.niffler.page.component.SearchField;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@ParametersAreNonnullByDefault
public class FriendsPage extends BasePage<FriendsPage> {

    private final ElementsCollection requestsTable = $$("[id='requests'] tr");
    private final ElementsCollection friendsTable = $$("[id='friends'] tr");
    private final SelenideElement noFriendsText = $(byText("There are no users yet"));

    private final SearchField searchField = new SearchField();
    private final AlertDialog alertDialog = new AlertDialog();

    @Step("Check friend '{friend}' is present in table")
    public void checkFriendIsPresentInTable(String friend) {
        searchField.search(friend);
        friendsTable.find(text(friend)).shouldBe(visible);
    }

    @Step("Check friends table is empty")
    public void checkFriendsTableIsEmpty() {
        noFriendsText.shouldBe(visible);
    }

    @Step("Check friend request from user'{income}' is present in table")
    public void checkFriendRequestFromUser(String income) {
        searchField.search(income);
        requestsTable.find(text(income)).$(byText("Accept")).shouldBe(visible);
    }

    private ElementsCollection getRowByUsername(String username) {
        return requestsTable
                .findBy(text(username))
                .$$("td");
    }

    @Step("Accept incoming request from {incomeUsername}")
    @Nonnull
    public FriendsPage acceptIncomingRequestFrom(String incomeUsername) {
        getRowByUsername(incomeUsername)
                .get(1)
                .$(byText("Accept"))
                .click();
        return this;
    }

    @Step("Decline incoming request from {incomeUsername}")
    @Nonnull
    public FriendsPage declineIncomingRequestFrom(String incomeUsername) {
        getRowByUsername(incomeUsername)
                .get(1)
                .$(byText("Decline"))
                .click();
        alertDialog.getDeclineButton().click();
        return this;
    }
}
