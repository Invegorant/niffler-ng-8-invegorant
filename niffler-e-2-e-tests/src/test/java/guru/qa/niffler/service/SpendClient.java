package guru.qa.niffler.service;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import java.util.UUID;

public interface SpendClient {

    SpendJson createSpend(SpendJson spend);

    SpendJson updateSpend(SpendJson spend);

    CategoryJson createCategory(CategoryJson category);

    CategoryJson findCategoryById(UUID id);

    CategoryJson findCategoryByUsernameAndCategoryName(String username, String name);

    SpendJson findSpendById(UUID id);

    SpendJson findSpendByUsernameAndSpendDescription(String username, String description);

    void removeSpend(SpendJson spend);

    void removeCategory(CategoryJson category);
}
