package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RegisterPage {

    private final SelenideElement signUpHeader = $("[class='header");
    private final SelenideElement logInLink = $(byText("Log in!"));
    private final SelenideElement usernameInput = $("input[name='username']");
    private final SelenideElement passwordInput = $("input[name='password']");
    private final SelenideElement showPasswordBtn = $("[id='passwordBtn']");
    private final SelenideElement submitPasswordInput = $("input[name='passwordSubmit']");
    private final SelenideElement showSubmitPasswordBtn = $("[id='passwordSubmitBtn']");
    private final SelenideElement signUpBtn = $("[class='form__submit']");
    private final SelenideElement signUpSuccessText = $("[class$='form__paragraph_success']");
    private final SelenideElement signInBtn = $("[class='form_sign-in']");

    public LoginPage fillUserDataAndSignUp(String username, String password, String passwordSubmit) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        submitPasswordInput.setValue(passwordSubmit);
        signUpBtn.click();

        signUpSuccessText.shouldBe(visible);
        signInBtn.click();

        return new LoginPage();
    }

    public RegisterPage setUsername(String username) {
        usernameInput.setValue(username);
        return this;
    }

    public RegisterPage setPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    public RegisterPage setPasswordSubmit(String passwordSubmit) {
        submitPasswordInput.setValue(passwordSubmit);
        return this;
    }

    public RegisterPage clickSignUp() {
        signUpBtn.click();
        return this;
    }

    public RegisterPage assertRegisterPageIsPresent() {
        signUpHeader.shouldHave(text("Sign up"));
        return this;
    }

    public RegisterPage showPassword() {
        showPasswordBtn.click();
        showPasswordBtn.shouldBe(Condition.attributeMatching("class", ".*active"));
        return this;
    }

    public RegisterPage showPasswordSubmit() {
        showSubmitPasswordBtn.click();
        showSubmitPasswordBtn.shouldBe(Condition.attributeMatching("class", ".*active"));
        return this;
    }

    public RegisterPage checkUserNameInput(String usernameValue) {
        usernameInput.shouldHave(value(usernameValue)
                .because("Заполненное поле Username должно иметь значение " + usernameValue));
        return this;
    }

    public RegisterPage checkPasswordInput(String passwordValue) {
        passwordInput.shouldHave(value(passwordValue)
                .because("Заполненное поле Password должно иметь значение " + passwordValue));
        return this;
    }

    public RegisterPage checkPasswordSubmitInput(String passwordValue) {
        assertEquals(passwordInput.val(), passwordValue,
                "Заполненное поле Password должно иметь значение " + passwordValue);
        return this;
    }

    public LoginPage clickLogInLink() {
        logInLink.click();
        return new LoginPage();
    }
}
