package guru.qa.niffler.api;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpendApiClient {

    private static final Config CFG = Config.getInstance();

    private final OkHttpClient client = new OkHttpClient.Builder().build();
    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(CFG.spendUrl())
            .client(client)
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final SpendApi spendApi = retrofit.create(SpendApi.class);

    private <T> T execute(Call<T> executeMethod, int expectedCode) {
        final Response<T> response;
        try {
            response = executeMethod.execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }

        assertEquals(expectedCode, response.code());
        return response.body();
    }


    public SpendJson addSpend(SpendJson spend) {
        return execute(spendApi.addSpend(spend), 201);
    }
    //ToDo добавить для остальных методов

    public SpendJson editSpend(SpendJson spend) {
        return execute(spendApi.editSpend(spend), 200);
    }

    public SpendJson getSpend(String id, String username) {
        return execute(spendApi.getSpend(id, username), 200);
    }

    public List<SpendJson> getSpends(String username, CurrencyValues filterCurrency, Date from, Date to) {
        return execute(spendApi.getSpends(username, filterCurrency, from, to), 200);
    }

    public Response<Void> deleteSpends(String username, List<String> ids) {
        return execute(spendApi.deleteSpends(username, ids), 200);
    }

    public CategoryJson addCategory(CategoryJson category) {
        return execute(spendApi.addCategory(category), 200);
    }

    public CategoryJson updateCategory(CategoryJson category) {
        return execute(spendApi.updateCategory(category), 200);
    }

    public List<CategoryJson> getCategories(String username, boolean excludeArchived) {
        return execute(spendApi.getCategories(username, excludeArchived), 200);
    }
}
