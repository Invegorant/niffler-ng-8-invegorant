package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.ProfilePage;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;

@WebTest
@Feature("Профиль")
public class ProfileTest extends AbstractTest {

    private static final String PROFILE_SUCCESSFULLY_UPDATED_MSG = "Profile successfully updated";

    @User(
            categories = @Category(
                    archived = true
            )
    )
    @Test
    @DisplayName("Профиль - Отображение архивной категории")
    void profile_archivedCategoryShouldPresentInCategoriesList(UserJson user) {
        final CategoryJson archivedCategory = user.testData().categories().getFirst();

        openLoginPage()
                .doLogin(user.username(), user.testData().password())
                .getHeader()
                .toProfilePage()
                .showArchivedCategories()
                .categoryIsPresent(archivedCategory.name());
    }

    @User(categories = @Category)
    @Test
    @DisplayName("Профиль - Отображение не архивной категории")
    void profile_activeCategoryShouldPresentInCategoriesList(UserJson user) {
        openLoginPage()
                .doLogin(user.username(), user.testData().password())
                .getHeader()
                .toProfilePage()
                .categoryIsPresent(user.testData().categories().getFirst().name());
    }

    @User
    @ScreenShotTest("img/e-commerce.png")
    void profile_shouldUpdateProfileImageWhenUploadNewImage(UserJson user, BufferedImage expected) throws IOException {
        ProfilePage profilePage = openLoginPage()
                .doLogin(user.username(), user.testData().password())
                .getHeader()
                .toProfilePage()
                .uploadPhoto("img/e-commerce.png")
                .submitProfile()
                .assertToastMessage(PROFILE_SUCCESSFULLY_UPDATED_MSG);

        Selenide.refresh();

        profilePage.assertProfilePic(expected);
    }

    @User
    @ScreenShotTest("img/e-commerce.png")
    void profile_shouldUpdateProfileImageWhenUpdateImage(UserJson user, BufferedImage expected) throws IOException {
        ProfilePage profilePage = openLoginPage()
                .doLogin(user.username(), user.testData().password())
                .getHeader()
                .toProfilePage()
                .uploadPhoto("img/jojo.png")
                .submitProfile()
                .assertToastMessage(PROFILE_SUCCESSFULLY_UPDATED_MSG);

        Selenide.refresh();

        profilePage
                .uploadPhoto("img/e-commerce.png")
                .submitProfile()
                .assertToastMessage(PROFILE_SUCCESSFULLY_UPDATED_MSG);

        Selenide.refresh();

        profilePage.assertProfilePic(expected);
    }

    @User
    @Test
    void profile_verifyUserNameIsDisplayedOnProfilePage(UserJson user) {
        login(user)
                .getHeader()
                .toProfilePage()
                .verifyUserNameIsDisplayed(user.username());
    }

    @User
    @Test
    void profile_verifyUserNameFieldIsDisabledOnProfilePage(UserJson user) {
        login(user)
                .getHeader()
                .toProfilePage()
                .verifyUserNameIsDisabled();
    }

    @User
    @Test
    void profile_verifyUserCanEditUserNameOnProfilePage(UserJson user) {
        login(user)
                .getHeader()
                .toProfilePage()
                .updateName("NewUserName")
                .verifyNameIsUpdated("NewUserName");
    }
}
