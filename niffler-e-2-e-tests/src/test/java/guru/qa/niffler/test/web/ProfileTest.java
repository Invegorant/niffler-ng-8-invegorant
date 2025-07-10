package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.rest.CategoryJson;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.ProfilePage;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static guru.qa.niffler.common.messages.ApplicationWarnings.PROFILE_UPDATED;

@WebTest
@Feature("Профиль")
public class ProfileTest extends AbstractTest {

    private static final String PROFILE_SUCCESSFULLY_UPDATED_MSG = PROFILE_UPDATED.getVal();

    @Test
    @User(
            categories = @Category(
                    archived = true
            )
    )
    @ApiLogin
    @DisplayName("Профиль - Отображение архивной категории")
    void profile_archivedCategoryShouldPresentInCategoriesList(UserJson user) {
        final CategoryJson archivedCategory = user.testData().categories().getFirst();

        new MainPage()
                .getHeader()
                .toProfilePage()
                .showArchivedCategories()
                .categoryIsPresent(archivedCategory.name());
    }

    @Test
    @User(categories = @Category)
    @ApiLogin
    @DisplayName("Профиль - Отображение не архивной категории")
    void profile_activeCategoryShouldPresentInCategoriesList(UserJson user) {
        new MainPage()
                .getHeader()
                .toProfilePage()
                .categoryIsPresent(user.testData().categories().getFirst().name());
    }

    @ScreenShotTest("img/e-commerce.png")
    @User
    @ApiLogin
    void profile_shouldUpdateProfileImageWhenUploadNewImage(BufferedImage expected) throws IOException {
        ProfilePage profilePage = new MainPage()
                .getHeader()
                .toProfilePage();

        profilePage.uploadPhoto("img/e-commerce.png")
                .submitProfile()
                .assertToastMessage(PROFILE_SUCCESSFULLY_UPDATED_MSG);

        Selenide.refresh();

        profilePage.assertProfilePic(expected);
    }

    @ScreenShotTest("img/e-commerce.png")
    @User
    @ApiLogin
    void profile_shouldUpdateProfileImageWhenUpdateImage(BufferedImage expected) throws IOException {
        ProfilePage profilePage = new MainPage()
                .getHeader()
                .toProfilePage();

        profilePage.uploadPhoto("img/jojo.png")
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

    @Test
    @User
    @ApiLogin
    void profile_verifyUserNameIsDisplayedOnProfilePage(UserJson user) {
        new MainPage()
                .getHeader()
                .toProfilePage()
                .verifyUserNameIsDisplayed(user.username());
    }

    @Test
    @User
    @ApiLogin
    void profile_verifyUserNameFieldIsDisabledOnProfilePage() {
        new MainPage()
                .getHeader()
                .toProfilePage()
                .verifyUserNameIsDisabled();
    }

    @Test
    @User
    @ApiLogin
    void profile_verifyUserCanEditUserNameOnProfilePage(UserJson user) {
        login(user)
                .getHeader()
                .toProfilePage()
                .updateName("NewUserName")
                .verifyNameIsUpdated("NewUserName");
    }
}
