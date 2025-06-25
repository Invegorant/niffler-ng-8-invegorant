package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

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

    @Step("Fill all fields, click sign in, return to Login Page")
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

    @Step("Set username: {username}")
    @Nonnull
    public RegisterPage setUsername(String username) {
        usernameInput.setValue(username);
        return this;
    }

    @Step("Set password: {password}")
    @Nonnull
    public RegisterPage setPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    @Step("Set password submit: {passwordSubmit}")
    @Nonnull
    public RegisterPage setPasswordSubmit(String passwordSubmit) {
        submitPasswordInput.setValue(passwordSubmit);
        return this;
    }

    @Step("Click Sign Up button")
    @Nonnull
    public RegisterPage clickSignUp() {
        signUpBtn.click();
        return this;
    }

    @Step("Verify 'Register Page' is displayed")
    @Nonnull
    public RegisterPage assertRegisterPageIsPresent() {
        signUpHeader.shouldHave(text("Sign up"));
        return this;
    }

    @Step("Click show password button")
    @Nonnull
    public RegisterPage showPassword() {
        showPasswordBtn.click();
        showPasswordBtn.shouldBe(Condition.attributeMatching("class", ".*active"));
        return this;
    }

    @Step("Click show password submit button")
    @Nonnull
    public RegisterPage showPasswordSubmit() {
        showSubmitPasswordBtn.click();
        showSubmitPasswordBtn.shouldBe(Condition.attributeMatching("class", ".*active"));
        return this;
    }

    @Step("Verify username input has value:  {usernameValue}")
    @Nonnull
    public RegisterPage checkUserNameInput(String usernameValue) {
        usernameInput.shouldHave(value(usernameValue)
                .because("Заполненное поле Username должно иметь значение " + usernameValue));
        return this;
    }

    @Step("Verify password input has value:  {passwordValue}")
    @Nonnull
    public RegisterPage checkPasswordInput(String passwordValue) {
        passwordInput.shouldHave(value(passwordValue)
                .because("Заполненное поле Password должно иметь значение " + passwordValue));
        return this;
    }

    @Step("Verify password submit input has value:  {passwordValue}")
    @Nonnull
    public RegisterPage checkPasswordSubmitInput(String passwordValue) {
        assertEquals(passwordInput.val(), passwordValue,
                "Заполненное поле Password должно иметь значение " + passwordValue);
        return this;
    }

    @Step("Click Log In!")
    @Nonnull
    public LoginPage clickLogInLink() {
        logInLink.click();
        return new LoginPage();
    }
}
