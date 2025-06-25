package guru.qa.niffler.page.component;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.page.*;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class Header {

    private static final String HEADER_TITLE = "Niffler";
    private final Menu menu = new Menu();
    private final SelenideElement self = $("#root header");

    private final String addSpendingLink = "a[href*='/spending']";

    @Step("Navigate to 'Add spending' page")
    @Nonnull
    public AddSpendingPage navigateToAddSpendingPage() {
        self.$(addSpendingLink).click();
        return new AddSpendingPage();
    }

    @Step("Verify header title is displayed")
    public void verifyHeaderTitle() {
        self.$("h1").shouldHave(text(HEADER_TITLE));
    }

    @Step("Navigate to Friends page")
    @Nonnull
    public FriendsPage toFriendsPage() {
        menu.openMenu().selectMenuItem(Menu.MenuItem.FRIENDS);
        return new FriendsPage();
    }

    @Step("Navigate to All People page")
    @Nonnull
    public AllPeoplePage toAllPeoplesPage() {
        menu.openMenu().selectMenuItem(Menu.MenuItem.ALL_PEOPLE);
        return new AllPeoplePage();
    }

    @Step("Navigate to Profile page")
    @Nonnull
    public ProfilePage toProfilePage() {
        menu.openMenu().selectMenuItem(Menu.MenuItem.PROFILE);
        return new ProfilePage();
    }

    @Step("Sign out")
    @Nonnull
    public LoginPage signOut() {
        menu.openMenu().selectMenuItem(Menu.MenuItem.SIGN_OUT);
        return new LoginPage();
    }

    @Step("Navigate to Main page")
    @Nonnull
    public MainPage toMainPage() {
        return Selenide.open(Config.getInstance().frontUrl(), MainPage.class);
    }

}
