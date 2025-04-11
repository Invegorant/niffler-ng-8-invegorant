package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoginPage extends AbstractPage {

    private final SelenideElement usernameInput = $("input[name='username']");
    private final SelenideElement passwordInput = $("input[name='password']");
    private final SelenideElement submitBtn = $("button[type='submit']");
    private final SelenideElement registryBtn = $("[class='form__register']");
    private final SelenideElement loginHeader = $("h1");
    private final SelenideElement showPasswordBtn = $("button[class^='form__password']");

    public MainPage doLogin(String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        submitBtn.click();
        return new MainPage();
    }

    public LoginPage setUsername(String username) {
        usernameInput.setValue(username);
        return this;
    }

    public LoginPage setPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    public RegisterPage openRegisterPage() {
        registryBtn.click();
        return new RegisterPage();
    }

    public void assertLoginPageIsOpened() {
        loginHeader.shouldHave(text("Log in"));
    }

    public LoginPage showPassword() {
        showPasswordBtn.click();
        showPasswordBtn.shouldBe(Condition.attributeMatching("class", ".*active"));
        return this;
    }

    public LoginPage checkUserNameInput(String usernameValue) {
        assertEquals(usernameInput.val(), usernameValue,
                "Заполненное поле Username должно иметь значение " + usernameValue);
        return this;
    }

    public LoginPage checkPasswordInput(String passwordValue) {
        assertEquals(passwordInput.val(), passwordValue,
                "Заполненное поле Password должно иметь значение " + passwordValue);
        return this;
    }
}
