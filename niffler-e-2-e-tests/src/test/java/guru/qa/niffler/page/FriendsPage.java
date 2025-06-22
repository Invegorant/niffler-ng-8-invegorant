package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

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
    private final SelenideElement searchInput = $("input");

    public void checkFriendIsPresentInTable(String friend) {
        searchInput.setValue(friend).pressEnter();
        friendsTable.find(text(friend)).shouldBe(visible);
    }

    public void checkFriendsTableIsEmpty() {
        noFriendsText.shouldBe(visible);
    }

    public void checkFriendRequestFromUser(String income) {
        searchInput.setValue(income).pressEnter();
        requestsTable.find(text(income)).$(byText("Accept")).shouldBe(visible);
    }
}
