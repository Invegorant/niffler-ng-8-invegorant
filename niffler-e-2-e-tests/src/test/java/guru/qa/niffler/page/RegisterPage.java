package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class RegisterPage extends BasePage<RegisterPage> {

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

    @Nonnull
    public LoginPage fillUserDataAndSignUp(String username, String password, String passwordSubmit) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        submitPasswordInput.setValue(passwordSubmit);
        signUpBtn.click();

        signUpSuccessText.shouldBe(visible);
        signInBtn.click();

        return new LoginPage();
    }

    @Nonnull
    public RegisterPage setUsername(String username) {
        usernameInput.setValue(username);
        return this;
    }

    @Nonnull
    public RegisterPage setPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    @Nonnull
    public RegisterPage setPasswordSubmit(String passwordSubmit) {
        submitPasswordInput.setValue(passwordSubmit);
        return this;
    }

    @Nonnull
    public RegisterPage clickSignUp() {
        signUpBtn.click();
        return this;
    }

    @Nonnull
    public RegisterPage assertRegisterPageIsPresent() {
        signUpHeader.shouldHave(text("Sign up"));
        return this;
    }

    @Nonnull
    public RegisterPage showPassword() {
        showPasswordBtn.click();
        showPasswordBtn.shouldBe(Condition.attributeMatching("class", ".*active"));
        return this;
    }

    @Nonnull
    public RegisterPage showPasswordSubmit() {
        showSubmitPasswordBtn.click();
        showSubmitPasswordBtn.shouldBe(Condition.attributeMatching("class", ".*active"));
        return this;
    }

    @Nonnull
    public RegisterPage checkUserNameInput(String usernameValue) {
        usernameInput.shouldHave(value(usernameValue)
                .because("Заполненное поле Username должно иметь значение " + usernameValue));
        return this;
    }

    @Nonnull
    public RegisterPage checkPasswordInput(String passwordValue) {
        passwordInput.shouldHave(value(passwordValue)
                .because("Заполненное поле Password должно иметь значение " + passwordValue));
        return this;
    }

    @Nonnull
    public RegisterPage checkPasswordSubmitInput(String passwordValue) {
        assertEquals(passwordInput.val(), passwordValue,
                "Заполненное поле Password должно иметь значение " + passwordValue);
        return this;
    }

    @Nonnull
    public LoginPage clickLogInLink() {
        logInLink.click();
        return new LoginPage();
    }
}
