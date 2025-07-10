package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.UserdataApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.UsersClient;
import io.qameta.allure.Step;
import org.apache.commons.lang3.time.StopWatch;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static guru.qa.niffler.model.FriendshipStatus.INVITE_RECEIVED;
import static guru.qa.niffler.model.FriendshipStatus.INVITE_SENT;
import static guru.qa.niffler.test.web.AbstractTest.DEFAULT_PASSWORD;
import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;
import static java.lang.Thread.sleep;
import static org.apache.hc.core5.http.HttpStatus.SC_OK;

@ParametersAreNonnullByDefault
public class UsersApiClient extends RestClient implements UsersClient {

    private static final String defaultPassword = DEFAULT_PASSWORD;

    private final UserdataApi userdataApi;
    private final AuthApiClient authApi = new AuthApiClient();

    public UsersApiClient() {
        super(Config.getInstance().userdataUrl());
        this.userdataApi = create(UserdataApi.class);
    }

    @Step("Create User")
    @NotNull
    @Override
    public UserJson createUser(String username, String password) {
        try {
            authApi.requestRegisterForm();
            authApi.register(
                    username,
                    password,
                    password,
                    ThreadSafeCookieStore.INSTANCE.getCookieValue("XSRF-TOKEN"));

            StopWatch sw = StopWatch.createStarted();
            while (sw.getTime(TimeUnit.SECONDS) < 5) {
                UserJson userJson = currentUser(username);
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

    @Step("Find all users with API")
    public List<UserJson> allUsers(String username, String searchQuery) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        return execute(userdataApi.allUsers(username, searchQuery), SC_OK);
    }

    @Step("Get current user using API")
    @Nonnull
    public UserJson currentUser(@Nonnull String username) {
        return execute(userdataApi.currentUser(username), SC_OK);
    }

    @Step("Get user's friends using API")
    @Nonnull
    public List<UserJson> getFriends(@Nonnull String username) {
        List<UserJson> resp = execute(userdataApi.friends(username, null), SC_OK);
        return !resp.isEmpty() ? resp : Collections.emptyList();
    }

    @Step("Get user's income invitations using API")
    @Nonnull
    public List<UserJson> getIncomeInvitations(@Nonnull String username) {
        List<UserJson> friends = getFriends(username);

        return friends.stream()
                .filter(userJson -> userJson.friendshipStatus().equals(INVITE_RECEIVED))
                .toList();
    }

    @Step("Get user's outcome invitations using API")
    @Nonnull
    public List<UserJson> getOutcomeInvitations(@Nonnull String username) {
        List<UserJson> allPeople = allUsers(username, null);

        return allPeople.stream()
                .filter(userJson -> userJson.friendshipStatus() != null)
                .filter(userJson -> userJson.friendshipStatus().equals(INVITE_SENT))
                .toList();
    }
}
