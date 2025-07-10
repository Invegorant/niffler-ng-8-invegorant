package guru.qa.niffler.test.web.alert;

import guru.qa.niffler.common.messages.ApplicationWarnings;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.test.web.AbstractTest;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.utils.RandomDataUtils.randomCategoryName;

@WebTest
@Feature("Аллерты - Профиль")
public class ProfilePageAlertTest extends AbstractTest {

    @User
    @Test
    @ApiLogin
    void profileAlert_shouldDisplaySuccessAlertWhenProfileNameUpdated() {
        new MainPage()
                .getHeader()
                .toProfilePage()
                .updateName("NewUserName")
                .checkAlertMessage(ApplicationWarnings.PROFILE_UPDATED.getVal());
    }

    @Test
    @ApiLogin
    @User
    void profileAlert_shouldDisplaySuccessAlertWhenCategoryAdded() {
        final String categoryName = randomCategoryName();
        new MainPage()
                .getHeader()
                .toProfilePage()
                .addCategory(categoryName)
                .checkAlertMessage(String.format(ApplicationWarnings.CATEGORY_ADDED.getVal(), categoryName));
    }

    @Test
    @ApiLogin
    @User
    void profileAlert_shouldDisplayErrorAlertWhenCategoryNameTooLong() {
        final String categoryName = randomCategoryName();
        new MainPage()
                .getHeader()
                .toProfilePage()
                .addCategory(categoryName)
                .checkAlertMessage(String.format(ApplicationWarnings.ERROR_WHILE_ADDING_CATEGORY.getVal(), categoryName));
    }
}
