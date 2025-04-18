package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.utils.RandomDataUtils;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@WebTest
@Feature("Регистрация")
public class RegistryTest extends AbstractTest {

    @Test
    @DisplayName("Регистрация - Успешная регистрация + авторизация")
    void registration_shouldRegisterNewUser() {
        final String username = RandomDataUtils.randomUsername();

        openLoginPage()
                .openRegisterPage()
                .fillUserDataAndSignUp(username, DEFAULT_PASSWORD, DEFAULT_PASSWORD)
                .doLogin(username, DEFAULT_PASSWORD)
                .checkStatisticsIsPresent()
                .checkHistoryOfSpendingIsPresent();
    }

    @Test
    @DisplayName("Регистрация - Неуспешная регистрация. УЗ уже существует")
    void registration_shouldNotRegisterUserWithExistingUser() {
        openLoginPage()
                .openRegisterPage()
                .setUsername(DEFAULT_USERNAME)
                .setPassword(DEFAULT_PASSWORD)
                .setPasswordSubmit(DEFAULT_PASSWORD)
                .clickSignUp();
        assertError("Username `" + DEFAULT_USERNAME + "` already exists");
    }

    @Test
    @DisplayName("Регистрация - Неуспешная регистрация. Пароли не совпадают")
    void registration_shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {
        openLoginPage()
                .openRegisterPage()
                .setUsername(DEFAULT_USERNAME)
                .setPassword(DEFAULT_PASSWORD)
                .setPasswordSubmit("wrong_password")
                .clickSignUp();
        assertError("Passwords should be equal");
    }

    @Test
    @DisplayName("Регистрация - Неуспешная регистрация. Пустое поле Username")
    void registration_shouldShowErrorIfLoginInputIsEmpty() {
        openLoginPage()
                .openRegisterPage()
                .setPassword(DEFAULT_PASSWORD)
                .setPasswordSubmit(DEFAULT_PASSWORD)
                .clickSignUp()
                .assertRegisterPageIsPresent();
    }

    @Test
    @DisplayName("Регистрация - Неуспешная регистрация. Пустое поле Password")
    void registration_shouldShowErrorIfPasswordInputIsEmpty() {
        openLoginPage()
                .openRegisterPage()
                .setUsername(DEFAULT_USERNAME)
                .setPasswordSubmit(DEFAULT_PASSWORD)
                .clickSignUp()
                .assertRegisterPageIsPresent();
    }

    @Test
    @DisplayName("Регистрация - Неуспешная регистрация. Пустое поле Submit Password")
    void registration_shouldShowErrorIfSubmitPasswordInputIsEmpty() {
        openLoginPage()
                .openRegisterPage()
                .setUsername(DEFAULT_USERNAME)
                .setPassword(DEFAULT_PASSWORD)
                .clickSignUp()
                .assertRegisterPageIsPresent();
    }

    @Test
    @DisplayName("Регистрация - Проверка заполненности полей")
    void registration_fieldsShouldHaveCorrectValues() {
        openLoginPage()
                .openRegisterPage()
                .setUsername(DEFAULT_USERNAME)
                .setPassword(DEFAULT_PASSWORD)
                .checkUserNameInput(DEFAULT_USERNAME)
                .showPassword()
                .checkPasswordInput(DEFAULT_PASSWORD)
                .showPasswordSubmit()
                .checkPasswordSubmitInput(DEFAULT_PASSWORD);
    }

    @Test
    @DisplayName("Регистрация - Возврат на страницу Авторизации")
    void registration_shouldRedirectToLoginPageAfterClickOnLink() {
        openLoginPage()
                .openRegisterPage()
                .clickLogInLink()
                .assertLoginPageIsOpened();
    }
}
