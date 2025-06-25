package guru.qa.niffler.service.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.repository.impl.hibernate.SpendRepositoryHibernate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.SpendClient;
import io.qameta.allure.Step;
import org.openqa.selenium.NotFoundException;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class SpendDbClient implements SpendClient {

    private static final Config CFG = Config.getInstance();

    private final SpendRepository spendRepository = new SpendRepositoryHibernate();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.spendJdbcUrl()
    );

    @Step("Create spend: {spend}")
    @Nonnull
    @Override
    public SpendJson createSpend(SpendJson spend) {
        return Objects.requireNonNull(xaTransactionTemplate.execute(() -> {
                    SpendEntity spendEntity = SpendEntity.fromJson(spend);
                    CategoryEntity categoryEntity = spendEntity.getCategory();
                    if (categoryEntity.getId() == null) {
                        String username = categoryEntity.getUsername();
                        String name = categoryEntity.getName();

                        Optional<CategoryEntity> existingCategoryOpt = spendRepository.findCategoryByUsernameAndCategoryName(username, name);
                        existingCategoryOpt.ifPresent(spendEntity::setCategory);
                    }

                    return SpendJson.fromEntity(spendRepository.createSpend(spendEntity));
                }
        ));
    }

    @Step("Update spend: {spend}")
    @Nonnull
    @Override
    public SpendJson updateSpend(SpendJson spend) {
        return Objects.requireNonNull(xaTransactionTemplate.execute(() -> {
            SpendEntity spendEntity = SpendEntity.fromJson(spend);
            return SpendJson.fromEntity(spendRepository.updateSpend(spendEntity));
        }));
    }

    @Step("Create category: {category}")
    @Nonnull
    @Override
    public CategoryJson createCategory(CategoryJson category) {
        return Objects.requireNonNull(xaTransactionTemplate.execute(() -> {
            CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
            return CategoryJson.fromEntity(spendRepository.createCategory(categoryEntity));
        }));
    }

    @Step("Find category by id: {id}")
    @Nonnull
    @Override
    public CategoryJson findCategoryById(UUID id) {
        return Objects.requireNonNull(xaTransactionTemplate.execute(() -> {
            if (id == null) {
                throw new IllegalArgumentException("Category id must not be null");
            }
            return CategoryJson.fromEntity(
                    spendRepository.findCategoryById(id).stream()
                            .findFirst()
                            .orElseThrow(() -> new NotFoundException("Category is not found by id: " + id))
            );
        }));
    }

    @Step("Find category by username '{username}' and categoryName '{name}'")
    @Nonnull
    @Override
    public CategoryJson findCategoryByUsernameAndCategoryName(String username, String name) {
        return Objects.requireNonNull(xaTransactionTemplate.execute(() -> {
            if (username == null || name == null || username.isEmpty() || name.isEmpty()) {
                throw new IllegalArgumentException("username and name must not be null or empty");
            }
            return CategoryJson.fromEntity(
                    spendRepository.findCategoryByUsernameAndCategoryName(username, name).stream()
                            .findFirst()
                            .orElseThrow(() -> new NotFoundException("Category is not found"))
            );
        }));
    }

    @Step("Find spend by id: {id}")
    @Nonnull
    @Override
    public SpendJson findSpendById(UUID id) {
        return Objects.requireNonNull(xaTransactionTemplate.execute(() -> {
            if (id == null) {
                throw new IllegalArgumentException("Spend id must not be null");
            }
            return SpendJson.fromEntity(
                    spendRepository.findSpendById(id).stream()
                            .findFirst()
                            .orElseThrow(() -> new NotFoundException("Spend is not found by id: " + id))
            );
        }));
    }

    @Step("Find spend by username '{username}' and description '{description}'")
    @Nonnull
    @Override
    public SpendJson findSpendByUsernameAndSpendDescription(String username, String description) {
        return Objects.requireNonNull(xaTransactionTemplate.execute(() -> {
            if (username == null || description == null || username.isEmpty() || description.isEmpty()) {
                throw new IllegalArgumentException("username and description must not be null or empty");
            }
            return SpendJson.fromEntity(
                    spendRepository.findByUsernameAndSpendDescription(username, description).stream()
                            .findFirst()
                            .orElseThrow(() -> new NotFoundException("Spend is not found"))
            );
        }));
    }

    @Step("Remove spend: {spend}")
    @Override
    public void removeSpend(SpendJson spend) {
        xaTransactionTemplate.execute(() -> {
                    if (spend.id() == null) {
                        throw new IllegalArgumentException("Spend id must be present");
                    }
                    spendRepository.removeSpend(SpendEntity.fromJson(spend));
                    return null;
                }
        );
    }

    @Step("Remove category: {category}")
    @Override
    public void removeCategory(CategoryJson category) {
        xaTransactionTemplate.execute(() -> {
                    if (category.id() == null) {
                        throw new IllegalArgumentException("Category id must be present");
                    }
                    spendRepository.removeCategory(CategoryEntity.fromJson(category));
                    return null;
                }
        );
    }
}
