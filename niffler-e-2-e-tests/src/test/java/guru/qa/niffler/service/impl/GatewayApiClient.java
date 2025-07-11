package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.GatewayApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.model.rest.*;
import io.qameta.allure.Step;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static guru.qa.niffler.model.FriendshipStatus.*;
import static java.util.Objects.requireNonNull;
import static org.apache.hc.core5.http.HttpStatus.SC_OK;

@ParametersAreNonnullByDefault
public class GatewayApiClient extends RestClient {

    private final GatewayApi gatewayApi;

    public GatewayApiClient() {
        super(CFG.gatewayUrl());
        this.gatewayApi = create(GatewayApi.class);
    }

    @Step("Get all friends & income invitations using /api/friends/all endpoint")
    @Nonnull
    public List<UserJson> allFriends(String bearerToken,
                                     @Nullable String searchQuery) {
        return requireNonNull(execute(gatewayApi.allFriends(bearerToken, searchQuery), 200));
    }

    @Step("Get all spends with external 'GET /api/categories/all'")
    public @NotNull List<CategoryJson> allCategories(String token, boolean excludeArchived) {
        return execute(gatewayApi.allCategories(token, excludeArchived), SC_OK);
    }

    @Step("Add category with external API 'POST api/categories/add'")
    public Optional<CategoryJson> addCategory(String token, CategoryJson category) {
        return Optional.of(execute(gatewayApi.addCategory(token, category), SC_OK));
    }

    @Step("Get all currencies with external 'GET api/currencies/all'")
    public @NotNull List<CurrencyJson> allCurrencies(String token) {
        return execute(gatewayApi.allCurrencies(token), SC_OK);
    }

    @Step("Add spend with external API 'POST /api/spends/add'")
    public Optional<SpendJson> addSpend(String token, SpendJson spend) {
        return Optional.of(execute(gatewayApi.addSpend(token, spend), SC_OK));
    }

    @Step("Edit spend with external API 'PATCH /api/spends/edit'")
    public Optional<SpendJson> editSpend(String token, SpendJson spend) {
        return Optional.of(execute(gatewayApi.editSpend(token, spend), SC_OK));
    }

    @Step("Remove spends with external API 'DELETE /api/spends/remove'")
    public void removeSpends(String token, @NotNull List<String> ids) {
        execute(gatewayApi.removeSpends(token, ids), SC_OK);
    }

    @Step("Update user with external API /api/users/update")
    public Optional<UserJson> updateUser(String token, UserJson user) {
        return Optional.of(execute(gatewayApi.updateUser(token, user), SC_OK));
    }

    @Step("Find all users with external API /api/users/all")
    public @Nonnull List<UserJson> allUsers(String token, @Nullable String searchQuery) {
        return execute(gatewayApi.allUsers(token, searchQuery), SC_OK);
    }

    @Step("Remove friend with external API 'DELETE /api/friends/remove'")
    public void removeFriend(String token, @Nonnull String friendName) {
        execute(gatewayApi.removeFriend(token, friendName), SC_OK);
    }

    @Step("Send invitation to user with external API 'POST /api/invitations/send'")
    public Optional<UserJson> sendInvitation(String token, UserJson friend) {
        return Optional.of(execute(gatewayApi.sendInvitation(token, new FriendJson(friend.username())), SC_OK));
    }

    @Step("Accept invitation from user with external API 'POST /api/invitations/accept'")
    public Optional<UserJson> acceptInvitation(String token, UserJson friend) {
        return Optional.of(execute(gatewayApi.acceptInvitation(token, new FriendJson(friend.username())), SC_OK));
    }

    @Step("Decline invitation from user with external API 'POST /api/invitations/decline'")
    public Optional<UserJson> declineInvitation(String token, UserJson sender) {
        return Optional.of(execute(gatewayApi.declineInvitation(token, new FriendJson(sender.username())), SC_OK));
    }

    @Step("Filter all confirmed friends for user received with 'GET /api/friends/all'")
    public @Nonnull List<UserJson> getConfirmedFriends(String token, @Nullable String searchQuery) {
        return allFriends(token, searchQuery).stream()
                .filter(u -> FRIEND.equals(u.friendshipStatus()))
                .collect(Collectors.toList());
    }

    @Step("Filter all income invitations for user received with 'GET /api/users/all'")
    public @Nonnull List<UserJson> getIncomeInvitations(String token, @Nullable String searchQuery) {
        return allFriends(token, searchQuery).stream()
                .filter(u -> INVITE_RECEIVED.equals(u.friendshipStatus()))
                .collect(Collectors.toList());
    }

    @Step("Filter all outcome invitations for user received with 'GET /api/users/all'")
    public @Nonnull List<UserJson> getOutcomeInvitations(String token, @Nullable String searchQuery) {
        return allUsers(token, searchQuery).stream()
                .filter(u -> INVITE_SENT.equals(u.friendshipStatus()))
                .collect(Collectors.toList());
    }
}
