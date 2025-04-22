package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Optional;

public class CategoryExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);
    private final SpendDbClient spendDbClient = new SpendDbClient();

    /**
     * Перед выполнением теста проверяем наличие аннотации Category:
     * - если ее нет -> пропускаем логику CategoryExtension
     * - если есть -> создаем категорию с флагом archived = false
     * Если требуется архивная категория - выполняется запрос на обновление категории с флагом archived = true
     */
    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .filter(annotation -> ArrayUtils.isNotEmpty(annotation.categories()))
                .ifPresent(annotation -> {
                    CategoryJson categoryJson = new CategoryJson(
                            null,
                            RandomDataUtils.randomCategoryName(),
                            annotation.username(),
                            annotation.categories()[0].archived()
                    );
                    CategoryJson createdCategory = spendDbClient.createCategory(categoryJson);
                    context.getStore(NAMESPACE).put(context.getUniqueId(), createdCategory);
                });
    }

    /**
     * После выполнения теста проверяем, если категория не является архивной -> архивируем
     */
    @Override
    public void afterEach(ExtensionContext context) {
        Optional.ofNullable(context.getStore(NAMESPACE).get(context.getUniqueId(), CategoryJson.class))
                .ifPresent(spendDbClient::deleteCategory);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), CategoryJson.class);
    }
}
