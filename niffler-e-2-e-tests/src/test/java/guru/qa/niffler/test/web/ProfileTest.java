package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.UserJson;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

//ToDo запустить этот класс и желательно все тесты (если все ок - коммититься и приступить к ДЗ
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

    @User(
            username = DEFAULT_USERNAME,
            categories = @Category(
                    archived = false
            )
    )
    @Test
    @DisplayName("Профиль - Отображение не архивной категории")
    void profile_activeCategoryShouldPresentInCategoriesList(CategoryJson[] category) {
        openLoginPage()
                .doLogin(DEFAULT_USERNAME, DEFAULT_PASSWORD)
                .openProfilePage()
                .categoryIsPresent(category[0].name());
    }
}
