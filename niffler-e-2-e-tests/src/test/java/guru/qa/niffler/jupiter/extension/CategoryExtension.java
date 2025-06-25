package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendClient;
import guru.qa.niffler.service.impl.SpendDbClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ParametersAreNonnullByDefault
public class CategoryExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);
    private final SpendClient spendClient = new SpendDbClient();

    /**
     * Перед выполнением теста проверяем наличие аннотации Category:
     * - если ее нет -> пропускаем логику CategoryExtension
     * - если есть -> создаем категорию с флагом archived = false
     * Если требуется архивная категория - выполняется запрос на обновление категории с флагом archived = true
     */
    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(userAnno -> {
                    if (ArrayUtils.isNotEmpty(userAnno.categories())) {

                        UserJson createdUser = UserExtension.createdUser();
                        final String username = createdUser != null
                                ? createdUser.username()
                                : userAnno.username();

                        final List<CategoryJson> createdCategories = new ArrayList<>();
                        for (Category categoryAnno : userAnno.categories()) {
                            final String categoryName = "".equals(categoryAnno.name())
                                    ? RandomDataUtils.randomCategoryName()
                                    : categoryAnno.name();

                            CategoryJson category = new CategoryJson(
                                    null,
                                    categoryName,
                                    username,
                                    categoryAnno.archived()
                            );
                            createdCategories.add(
                                    spendClient.createCategory(category)
                            );
                        }


                        if (createdUser != null) {
                            createdUser.testData().categories().addAll(
                                    createdCategories
                            );
                        } else {
                            context.getStore(NAMESPACE).put(
                                    context.getUniqueId(),
                                    createdCategories
                            );
                        }
                    }
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson[].class);
    }

    public static @Nonnull List<CategoryJson> createdCategories() {
        final ExtensionContext context = TestsMethodContextExtension.context();
        return Optional.ofNullable(context.getStore(NAMESPACE).get(context.getUniqueId(), List.class))
                .orElse(Collections.emptyList());
    }

    @Override
    @SuppressWarnings("unchecked")
    public CategoryJson[] resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return createdCategories().toArray(CategoryJson[]::new);
    }
}
