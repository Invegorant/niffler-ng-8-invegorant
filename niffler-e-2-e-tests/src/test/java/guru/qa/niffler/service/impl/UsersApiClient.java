package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.UserdataApi;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.service.api.Execute;
import io.qameta.allure.Step;
import io.qameta.allure.okhttp3.AllureOkHttp3;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.commons.lang3.time.StopWatch;
import org.jetbrains.annotations.NotNull;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.TimeUnit;

import static guru.qa.niffler.test.web.AbstractTest.DEFAULT_PASSWORD;
import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;
import static java.lang.Thread.sleep;

@ParametersAreNonnullByDefault
public class UsersApiClient implements UsersClient, Execute {

    private static final Config CFG = Config.getInstance();
    private static final String defaultPassword = DEFAULT_PASSWORD;
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

    private final AuthApi authApi = retrofit.create(AuthApi.class);
    private final UserdataApi userdataApi = retrofit.create(UserdataApi.class);

    @Step("Create User")
    @NotNull
    @Override
    public UserJson createUser(String username, String password) {
        try {
            execute(authApi.requestRegisterForm());
            execute(authApi.register(
                    username,
                    password,
                    password,
                    ThreadSafeCookieStore.INSTANCE.getCookieValue("XSRF-TOKEN")));

            StopWatch sw = StopWatch.createStarted();
            while (sw.getTime(TimeUnit.SECONDS) < 5) {
                UserJson userJson = execute(userdataApi.currentUser(username));
                if (userJson != null && userJson.id() != null) {
                    return userJson.withEmptyTestData();
                }
                sleep(100);
            }
            throw new AssertionError("User creation timed out");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Step("Add income invitations to user: {targetUser}")
    @Override
    public void createIncomeInvitations(UserJson targetUser, int count) {
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final String username = randomUsername();

                UserJson user = createUser(username, defaultPassword);
                execute(userdataApi.sendInvitation(user.username(), targetUser.username()), 200);
            }
        }
    }

    @Step("Add outcome invitations to user: {targetUser}")
    @Override
    public void createOutcomeInvitations(UserJson targetUser, int count) {
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final String username = randomUsername();

                UserJson user = createUser(username, defaultPassword);
                execute(userdataApi.sendInvitation(targetUser.username(), user.username()), 200);
            }
        }
    }

    @Step("Add friends to user: {targetUser}")
    @Override
    public void createFriends(UserJson targetUser, int count) {
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final String username = randomUsername();

                UserJson user = createUser(username, defaultPassword);
                execute(userdataApi.sendInvitation(user.username(), targetUser.username()), 200);
                execute(userdataApi.acceptInvitation(targetUser.username(), username), 200);
            }
        }
    }

    @Step("Delete user: {user}")
    @Override
    public void remove(UserJson user) {
        throw new UnsupportedOperationException("NYI method remove");
    }
}
