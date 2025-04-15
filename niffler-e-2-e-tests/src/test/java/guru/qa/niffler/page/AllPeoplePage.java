package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$$;

public class AllPeoplePage {

    private final ElementsCollection allPeopleTable = $$("[id='all'] tr");

    public void checkOutcomeRequestToUser(String username) {
        getRowByUsername(username).$(byText("Waiting...")).shouldBe(visible);
    }

    private SelenideElement getRowByUsername(String username) {
        return allPeopleTable.find(text(username));
    }

}
