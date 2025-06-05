package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.UserJson;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@WebTest
@Feature("Профиль")
public class ProfileTest extends AbstractTest {

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
                .openProfilePage()
                .showArchivedCategories()
                .categoryIsPresent(archivedCategory.name());
    }

    @User(categories = @Category)
    @Test
    @DisplayName("Профиль - Отображение не архивной категории")
    void profile_activeCategoryShouldPresentInCategoriesList(UserJson user) {
        openLoginPage()
                .doLogin(user.username(), user.testData().password())
                .openProfilePage()
                .categoryIsPresent(user.testData().categories().getFirst().name());
    }
}
