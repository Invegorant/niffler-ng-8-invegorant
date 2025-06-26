package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface SpendRepository {

    @Nonnull
    SpendEntity createSpend(SpendEntity spend);

    @Nonnull
    SpendEntity updateSpend(SpendEntity spend);

    @Nonnull
    CategoryEntity createCategory(CategoryEntity category);

    @Nonnull
    Optional<CategoryEntity> findCategoryById(UUID id);

    @Nonnull
    Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String name);

    @Nonnull
    Optional<SpendEntity> findSpendById(UUID id);

    @Nonnull
    Optional<SpendEntity> findByUsernameAndSpendDescription(String username, String description);

    void removeSpend(SpendEntity spend);

    void removeCategory(CategoryEntity category);
}
