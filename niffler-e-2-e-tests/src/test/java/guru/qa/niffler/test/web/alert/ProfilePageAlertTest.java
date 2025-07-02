package guru.qa.niffler.test.web.alert;

import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.test.web.AbstractTest;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.common.messages.ApplicationWarnings.ProfileAlertMessages.*;
import static guru.qa.niffler.utils.RandomDataUtils.randomCategoryName;

@WebTest
@Feature("Аллерты - Профиль")
public class ProfilePageAlertTest extends AbstractTest {

    @User
    @Test
    void profileAlert_shouldDisplaySuccessAlertWhenProfileNameUpdated(UserJson user) {
        login(user)
                .getHeader()
                .toProfilePage()
                .updateName("NewUserName")
                .checkAlertMessage(PROFILE_UPDATED);
    }

    @User()
    @Test
    void profileAlert_shouldDisplaySuccessAlertWhenCategoryAdded(UserJson user) {
        final String categoryName = randomCategoryName();
        login(user)
                .getHeader()
                .toProfilePage()
                .addCategory(categoryName)
                .checkAlertMessage(String.format(CATEGORY_ADDED, categoryName));
    }

    @User()
    @Test
    void profileAlert_shouldDisplayErrorAlertWhenCategoryNameTooLong(UserJson user) {
        final String categoryName = randomCategoryName();
        login(user)
                .getHeader()
                .toProfilePage()
                .addCategory(categoryName)
                .checkAlertMessage(String.format(ERROR_WHILE_ADDING_CATEGORY, categoryName));
    }
}
