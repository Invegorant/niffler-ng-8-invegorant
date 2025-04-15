package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.components.Header;
import guru.qa.niffler.page.components.MenuComponent;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

/**
 * Класс предназначен как набор часто используемых тестовых методов/данных
 */
@ExtendWith(BrowserExtension.class)
public abstract class AbstractTest {

    private static final Config CFG = Config.getInstance();
    protected static final String DEFAULT_USERNAME = "Invegorant";
    protected static final String DEFAULT_PASSWORD = "12345";

    public LoginPage openLoginPage() {
        return Selenide.open(CFG.frontUrl(), LoginPage.class);
    }

    public LoginPage signOut() {
        new Header().openMenu().selectMenuItem(MenuComponent.MenuItem.SIGN_OUT);
        return new LoginPage();
    }

    public void assertError(String errorText) {
        $("[class='form__error']").shouldHave(text(errorText));
    }
}
