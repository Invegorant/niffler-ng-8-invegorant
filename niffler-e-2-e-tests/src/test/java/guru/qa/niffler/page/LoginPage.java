package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class LoginPage extends BasePage<LoginPage> {

    public static final String URL = Config.getInstance().authUrl() + "login";

    private final SelenideElement usernameInput;
    private final SelenideElement passwordInput;
    private final SelenideElement submitBtn;
    private final SelenideElement registryBtn;
    private final SelenideElement loginHeader;
    private final SelenideElement showPasswordBtn;

    public LoginPage(SelenideDriver driver) {
        this.usernameInput = driver.$("input[name='username']");
        this.passwordInput = driver.$("input[name='password']");
        this.submitBtn = driver.$("button[type='submit']");
        this.registryBtn = driver.$("[class='form__register']");
        this.loginHeader = driver.$("h1");
        this.showPasswordBtn = driver.$("button[class^='form__password']");
    }

    public LoginPage() {
        this.usernameInput = $("input[name='username']");
        this.passwordInput = $("input[name='password']");
        this.submitBtn = $("button[type='submit']");
        this.registryBtn = $("[class='form__register']");
        this.loginHeader = $("h1");
        this.showPasswordBtn = $("button[class^='form__password']");
    }

    @Nonnull
    public MainPage doLogin(String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        submitBtn.click();
        return new MainPage();
    }

    @Nonnull
    public LoginPage setUsername(String username) {
        usernameInput.setValue(username);
        return this;
    }

    @Nonnull
    public LoginPage setPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    @Nonnull
    public LoginPage submit() {
        submitBtn.click();
        return this;
    }

    @Nonnull
    public RegisterPage openRegisterPage() {
        registryBtn.click();
        return new RegisterPage();
    }

    public void assertLoginPageIsOpened() {
        loginHeader.shouldHave(text("Log in"));
    }

    @Nonnull
    public LoginPage showPassword() {
        showPasswordBtn.click();
        showPasswordBtn.shouldBe(Condition.attributeMatching("class", ".*active"));
        return this;
    }

    @Nonnull
    public LoginPage checkUserNameInput(String usernameValue) {
        usernameInput.shouldHave(value(usernameValue)
                .because("Заполненное поле Username должно иметь значение " + usernameValue));
        return this;
    }

    @Nonnull
    public LoginPage checkPasswordInput(String passwordValue) {
        passwordInput.shouldHave(value(passwordValue)
                .because("Заполненное поле Password должно иметь значение " + passwordValue));
        return this;
    }
}
