package guru.qa.niffler.page;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.page.components.Header;
import guru.qa.niffler.page.components.MenuComponent;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

/**
 * Класс предназначен как набор часто используемых тестовых методов/данных
 */
@ExtendWith(BrowserExtension.class)
public abstract class AbstractPage {

    private static final Config CFG = Config.getInstance();
    protected static final String DEFAULT_USERNAME = "Invegorant";
    protected static final String DEFAULT_PASSWORD = "12345";

    public LoginPage openLoginPage() {
        return Selenide.open(CFG.frontUrl(), LoginPage.class);
    }

    public MenuComponent openMenu() {
        return new Header().openMenu();
    }

    public ProfilePage openProfilePage() {
        openMenu().selectMenuItem(MenuComponent.MenuItem.PROFILE);
        return new ProfilePage();
    }

    public FriendsPage openFriendsPage() {
        openMenu().selectMenuItem(MenuComponent.MenuItem.FRIENDS);
        return new FriendsPage();
    }

    public AllPeoplePage openAllPeoplePage() {
        openMenu().selectMenuItem(MenuComponent.MenuItem.ALL_PEOPLE);
        return new AllPeoplePage();
    }

    public LoginPage signOut() {
        openMenu().selectMenuItem(MenuComponent.MenuItem.SIGN_OUT);
        return new LoginPage();
    }

    public void assertError(String errorText) {
        $("[class='form__error']").shouldHave(text(errorText));
    }
}
