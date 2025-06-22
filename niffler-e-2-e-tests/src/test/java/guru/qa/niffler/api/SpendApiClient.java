package guru.qa.niffler.api;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.api.BaseApiClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Date;
import java.util.List;

@ParametersAreNonnullByDefault
public class SpendApiClient extends BaseApiClient {

    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Config.getInstance().spendUrl())
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final SpendApi spendApi = retrofit.create(SpendApi.class);

    @Nullable
    public SpendJson addSpend(SpendJson spend) {
        return execute(spendApi.addSpend(spend), 201);
    }

    @Nullable
    public SpendJson editSpend(SpendJson spend) {
        return execute(spendApi.editSpend(spend), 200);
    }

    @Nullable
    public SpendJson getSpend(String id, String username) {
        return execute(spendApi.getSpend(id, username), 200);
    }

    @Nonnull
    public List<SpendJson> getSpends(String username,
                                     @Nullable CurrencyValues filterCurrency,
                                     @Nullable Date from,
                                     @Nullable Date to) {
        return execute(spendApi.getSpends(username, filterCurrency, from, to), 200);
    }

    public String deleteSpends(String username, List<String> ids) {
        return execute(spendApi.deleteSpends(username, ids), 200);
    }

    @Nullable
    public CategoryJson addCategory(CategoryJson category) {
        return execute(spendApi.addCategory(category), 200);
    }

    @Nullable
    public CategoryJson updateCategory(CategoryJson category) {
        return execute(spendApi.updateCategory(category), 200);
    }

    @Nonnull
    public List<CategoryJson> getCategories(String username, boolean excludeArchived) {
        return execute(spendApi.getCategories(username, excludeArchived), 200);
    }
}
