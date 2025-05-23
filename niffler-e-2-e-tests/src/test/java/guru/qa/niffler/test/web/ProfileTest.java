package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.CategoryJson;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@WebTest
@Feature("Профиль")
public class ProfileTest extends AbstractTest {

    @User(
            username = DEFAULT_USERNAME,
            categories = @Category(
                    archived = true
            )
    )
    @Test
    @DisplayName("Профиль - Отображение архивной категории")
    void profile_archivedCategoryShouldPresentInCategoriesList(CategoryJson category) {
        final String categoryName = category.name();

        openLoginPage()
                .doLogin(DEFAULT_USERNAME, DEFAULT_PASSWORD)
                .openProfilePage()
                .showArchivedCategories()
                .categoryIsPresent(categoryName);
    }

    @User(
            username = DEFAULT_USERNAME,
            categories = @Category(
                    archived = false
            )
    )
    @Test
    @DisplayName("Профиль - Отображение не архивной категории")
    void profile_activeCategoryShouldPresentInCategoriesList(CategoryJson category) {
        final String categoryName = category.name();

        openLoginPage()
                .doLogin(DEFAULT_USERNAME, DEFAULT_PASSWORD)
                .openProfilePage()
                .categoryIsPresent(categoryName);
    }
}
