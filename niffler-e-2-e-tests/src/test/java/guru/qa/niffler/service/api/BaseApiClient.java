package guru.qa.niffler.service.api;

import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class BaseApiClient {

    protected <T> T execute(Call<T> executeMethod, int expectedCode) {
        final Response<T> response;
        try {
            response = executeMethod.execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }

        assertEquals(expectedCode, response.code());
        return response.body();
    }

    protected <T> T execute(Call<T> executeMethod) {
        final Response<T> response;
        try {
            response = executeMethod.execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }

        return response.body();
    }
}
