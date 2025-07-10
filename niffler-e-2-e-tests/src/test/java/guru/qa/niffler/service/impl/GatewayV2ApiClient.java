package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.GatewayV2Api;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.model.pageable.RestResponsePage;
import guru.qa.niffler.model.rest.UserJson;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public class GatewayV2ApiClient extends RestClient {

    private final GatewayV2Api gatewayApi;

    public GatewayV2ApiClient() {
        super(CFG.gatewayUrl());
        this.gatewayApi = create(GatewayV2Api.class);
    }

    @Step("Get all friends & income invitations using /api/v2/friends/all endpoint")
    @Nonnull
    public RestResponsePage<UserJson> allFriends(String bearerToken,
                                                 int page,
                                                 int size,
                                                 @Nullable String searchQuery) {
        return requireNonNull(execute(gatewayApi.allFriends(bearerToken, page, size, searchQuery), 200));
    }
}