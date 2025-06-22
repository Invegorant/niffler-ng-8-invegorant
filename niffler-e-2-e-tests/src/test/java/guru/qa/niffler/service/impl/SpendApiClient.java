package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.SpendApi;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.SpendClient;
import guru.qa.niffler.service.api.BaseApiClient;
import io.qameta.allure.okhttp3.AllureOkHttp3;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.jetbrains.annotations.NotNull;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class SpendApiClient extends BaseApiClient implements SpendClient {

    private static final Config CFG = Config.getInstance();

    private final OkHttpClient client = new OkHttpClient.Builder()
            .addNetworkInterceptor(
                    new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addNetworkInterceptor(
                    new AllureOkHttp3()
                            .setRequestTemplate("http-request.ftl")
                            .setResponseTemplate("http-response.ftl")
            ).build();
    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(CFG.spendUrl())
            .client(client)
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final SpendApi spendApi = retrofit.create(SpendApi.class);


    @Nonnull
    @NotNull
    @Override
    public SpendJson createSpend(SpendJson spend) {
        return execute(spendApi.addSpend(spend), 201);
    }

    @NotNull
    @Override
    public SpendJson updateSpend(SpendJson spend) {
        return execute(spendApi.editSpend(spend), 200);
    }

    @NotNull
    @Override
    public CategoryJson createCategory(CategoryJson category) {
        return execute(spendApi.addCategory(category), 200);
    }

    @NotNull
    @Override
    public CategoryJson findCategoryById(UUID id) {
        throw new UnsupportedOperationException("NYI method findCategoryById");
    }

    @NotNull
    @Override
    public CategoryJson findCategoryByUsernameAndCategoryName(String username, String name) {
        throw new UnsupportedOperationException("NYI method findCategoryByUsernameAndCategoryName");
    }

    @NotNull
    @Override
    public SpendJson findSpendById(UUID id) {
        throw new UnsupportedOperationException("NYI method findById");
    }

    @NotNull
    @Override
    public SpendJson findSpendByUsernameAndSpendDescription(String username, String description) {
        throw new UnsupportedOperationException("NYI method findSpendByUsernameAndSpendDescription");
    }

    @Override
    public void removeSpend(SpendJson spend) {
        execute(spendApi.deleteSpends(spend.username(), List.of(String.valueOf(spend.id()))), 200);
    }

    @Override
    public void removeCategory(CategoryJson category) {
        throw new UnsupportedOperationException("NYI method removeCategory");
    }
}
