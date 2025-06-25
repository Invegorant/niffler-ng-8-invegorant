package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

/**
 * Класс предназначен как набор часто используемых тестовых методов/данных
 */
public abstract class AbstractTest {

    private static final Config CFG = Config.getInstance();
    protected static final String DEFAULT_USERNAME = "Invegorant";
    public static final String DEFAULT_PASSWORD = "12345";

    @Step("Open Login Page")
    public LoginPage openLoginPage() {
        return Selenide.open(CFG.frontUrl(), LoginPage.class);
    }

    @Step("Assert Error Text")
    public void assertError(String errorText) {
        $("[class='form__error']").shouldHave(text(errorText));
    }

    @Step("Open Main Page")
    public MainPage openMainPage() {
        return Selenide.open(CFG.frontUrl(), MainPage.class);
    }

    @Step("Login as {user}")
    public MainPage login(UserJson user) {
        return openLoginPage()
                .doLogin(user.username(), user.testData().password());
    }
}
