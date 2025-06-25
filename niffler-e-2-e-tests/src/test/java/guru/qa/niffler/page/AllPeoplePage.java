package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

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

    @Step("Verify outcome request from user: {username}")
    public void checkOutcomeRequestToUser(String username) {
        getRowByUsername(username).$(byText("Waiting...")).shouldBe(visible);
    }

    @Step("Search username: {username}")
    @Nonnull
    public AllPeoplePage searchRequestByUsername(String username) {
        searchInput.setValue(username).pressEnter();
        return this;
    }

    @Nonnull
    private SelenideElement getRowByUsername(String username) {
        return allPeopleTable.find(text(username));
    }

}
