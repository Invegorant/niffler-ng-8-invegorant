package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class FriendsPage {

    private final ElementsCollection requestsTable = $$("[id='requests'] tr");
    private final ElementsCollection friendsTable = $$("[id='friends'] tr");
    private final SelenideElement noFriendsText = $(byText("There are no users yet"));

    public void checkFriendIsPresentInTable(String friend) {
        friendsTable.find(text(friend)).shouldBe(visible);
    }

    public void checkFriendsTableIsEmpty() {
        noFriendsText.shouldBe(visible);
    }

    public void checkFriendRequestFromUser(String income) {
        requestsTable.find(text(income)).$(byText("Accept")).shouldBe(visible);
    }
}
