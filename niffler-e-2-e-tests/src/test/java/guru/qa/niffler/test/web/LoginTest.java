package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.page.LoginPage;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@WebTest
@Feature("Авторизация")
public class LoginTest extends AbstractTest {

    @Test
    @DisplayName("Авторизация - Успешная авторизация")
    void auth_mainPageShouldBeDisplayedAfterSuccessLogin() {
        openLoginPage()
                .doLogin(DEFAULT_USERNAME, DEFAULT_PASSWORD)
                .checkStatisticsIsPresent()
                .checkHistoryOfSpendingIsPresent();
    }

    @Test
    @DisplayName("Авторизация - Неверные логин/пароль")
    void auth_userShouldStayOnLoginPageAfterLoginWithBadCredentials() {
        openLoginPage()
                .doLogin(DEFAULT_USERNAME, "wrong_password");
        assertError("Неверные учетные данные пользователя");
    }

    @Test
    @DisplayName("Авторизация - Пустые поля логин и пароль")
    void auth_userShouldStayOnLoginPageAfterLoginWithEmptyFields() {
        openLoginPage()
                .doLogin(null, null);
        new LoginPage().assertLoginPageIsOpened();
    }

    @Test
    @DisplayName("Авторизация - Пустое поле логин")
    void auth_userShouldStayOnLoginPageAfterLoginWithEmptyLoginField() {
        openLoginPage()
                .doLogin(null, DEFAULT_PASSWORD);
        new LoginPage().assertLoginPageIsOpened();
    }

    @Test
    @DisplayName("Авторизация - Пустое поле пароль")
    void auth_userShouldStayOnLoginPageAfterLoginWithEmptyPasswordField() {
        openLoginPage()
                .doLogin(AbstractTest.DEFAULT_USERNAME, null);
        new LoginPage().assertLoginPageIsOpened();
    }


    @Test
    @DisplayName("Авторизация - Проверка заполнения полей")
    void auth_checkFieldsHasCorrectValuesAfterSetValues() {
        openLoginPage()
                .setUsername(DEFAULT_USERNAME)
                .setPassword(DEFAULT_PASSWORD)
                .showPassword()
                .checkUserNameInput(DEFAULT_USERNAME)
                .checkPasswordInput(DEFAULT_PASSWORD);
    }
}
