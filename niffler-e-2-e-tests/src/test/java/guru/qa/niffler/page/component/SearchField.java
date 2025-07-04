package guru.qa.niffler.page.component;

import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class SearchField extends BaseComponent<SearchField> {

    private final String searchInput = "input[aria-label='search']";
    private final String clearButton = "#input-clear";

    public SearchField() {
        super($("form.MuiBox-root"));
    }

    @Step("Enter search input: {description}")
    @Nonnull
    public SearchField enterSearchValue(String value) {
        self.$(searchInput).setValue(value).pressEnter();
        return this;
    }

    @Step("Clear search input if not empty")
    @Nonnull
    public SearchField clearIfNotEmpty() {
        if (self.$(clearButton).isDisplayed()) {
            self.$(clearButton).click();
        }
        return this;
    }

    @Step("Search for description: {description}")
    @Nonnull
    public SearchField search(String value) {
        clearIfNotEmpty();
        enterSearchValue(value);
        return this;
    }
}
