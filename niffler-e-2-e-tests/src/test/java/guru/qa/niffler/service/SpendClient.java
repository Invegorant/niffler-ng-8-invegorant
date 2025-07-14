package guru.qa.niffler.service;

import guru.qa.niffler.model.rest.CategoryJson;
import guru.qa.niffler.model.rest.SpendJson;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface SpendClient {

    @Nonnull
    SpendJson createSpend(SpendJson spend);

    @Nonnull
    SpendJson updateSpend(SpendJson spend);

    @Nonnull
    CategoryJson createCategory(CategoryJson category);

    @Nonnull
    CategoryJson findCategoryById(UUID id);

    @Nonnull
    CategoryJson findCategoryByUsernameAndCategoryName(String username, String name);

    @Nonnull
    SpendJson findSpendById(UUID id);

    @Nonnull
    SpendJson findSpendByUsernameAndSpendDescription(String username, String description);

    void removeSpend(SpendJson spend);

    void removeCategory(CategoryJson category);
}
