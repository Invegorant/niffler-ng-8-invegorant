package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@ParametersAreNonnullByDefault
public class AllPeoplePage extends BasePage<AllPeoplePage> {

    private final ElementsCollection allPeopleTable = $$("[id='all'] tr");
    private final SelenideElement searchInput = $("input");

    public void checkOutcomeRequestToUser(String username) {
        getRowByUsername(username).$(byText("Waiting...")).shouldBe(visible);
    }

    @Nonnull
    public AllPeoplePage searchRequestByUsername(String username) {
        searchInput.setValue(username).pressEnter();
        return this;
    }

    private SelenideElement getRowByUsername(String username) {
        return allPeopleTable.find(text(username));
    }

}
