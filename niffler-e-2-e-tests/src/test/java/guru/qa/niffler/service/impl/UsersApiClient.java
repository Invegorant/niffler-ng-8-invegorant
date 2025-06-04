package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.UserdataApi;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.service.api.BaseApiClient;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static guru.qa.niffler.test.web.AbstractTest.DEFAULT_PASSWORD;
import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;

public class UsersApiClient extends BaseApiClient implements UsersClient {

    private static final Config CFG = Config.getInstance();
    private static final String defaultPassword = DEFAULT_PASSWORD;
    private final OkHttpClient client = new OkHttpClient.Builder().build();
    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(CFG.spendUrl())
            .client(client)
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final AuthApi authApi = retrofit.create(AuthApi.class);
    private final UserdataApi userdataApi = retrofit.create(UserdataApi.class);

    @Override
    public UserJson createUser(String username, String password) {
        execute(authApi.requestRegisterForm());
        execute(authApi.register(username, password, password, null));
        UserJson createdUser = execute(userdataApi.currentUser(username));

        return createdUser.withPassword(
                defaultPassword
        );
    }

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

    @Override
    public void remove(UserJson user) {
        throw new UnsupportedOperationException("NYI method remove");
    }
}
