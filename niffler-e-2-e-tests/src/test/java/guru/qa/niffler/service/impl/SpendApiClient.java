package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.SpendApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.SpendClient;
import io.qameta.allure.Step;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class SpendApiClient extends RestClient implements SpendClient {

    private final SpendApi spendApi;

    public SpendApiClient() {
        super(Config.getInstance().spendUrl());
        this.spendApi = create(SpendApi.class);
    }

    @Step("Create spend: {spend}")
    @NotNull
    @Override
    public SpendJson createSpend(SpendJson spend) {
        return execute(spendApi.addSpend(spend), 201);
    }

    @Step("Update spend: {spend}")
    @NotNull
    @Override
    public SpendJson updateSpend(SpendJson spend) {
        return execute(spendApi.editSpend(spend), 200);
    }

    @Step("Create category: {category}")
    @NotNull
    @Override
    public CategoryJson createCategory(CategoryJson category) {
        return execute(spendApi.addCategory(category), 200);
    }

    @Step("Find category by id: {id}")
    @NotNull
    @Override
    public CategoryJson findCategoryById(UUID id) {
        throw new UnsupportedOperationException("NYI method findCategoryById");
    }

    @Step("Find category by username '{username}' and categoryName '{name}'")
    @NotNull
    @Override
    public CategoryJson findCategoryByUsernameAndCategoryName(String username, String name) {
        throw new UnsupportedOperationException("NYI method findCategoryByUsernameAndCategoryName");
    }

    @Step("Find spend by id: {id}")
    @NotNull
    @Override
    public SpendJson findSpendById(UUID id) {
        throw new UnsupportedOperationException("NYI method findById");
    }

    @Step("Find spend by username '{username}' and description '{description}'")
    @NotNull
    @Override
    public SpendJson findSpendByUsernameAndSpendDescription(String username, String description) {
        throw new UnsupportedOperationException("NYI method findSpendByUsernameAndSpendDescription");
    }

    @Step("Remove spend: {spend}")
    @Override
    public void removeSpend(SpendJson spend) {
        execute(spendApi.deleteSpends(spend.username(), List.of(String.valueOf(spend.id()))), 200);
    }

    @Step("Remove category: {category}")
    @Override
    public void removeCategory(CategoryJson category) {
        throw new UnsupportedOperationException("NYI method removeCategory");
    }
}
