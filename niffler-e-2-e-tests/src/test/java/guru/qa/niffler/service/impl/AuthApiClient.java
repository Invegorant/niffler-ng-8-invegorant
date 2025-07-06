package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.core.RestClient;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

public class AuthApiClient extends RestClient {

    private final AuthApi authApi;

    public AuthApiClient() {
        super(CFG.authUrl());
        authApi = create(AuthApi.class);
    }

    @Step("Register new user using REST API")
    public void register(@Nonnull String csrf,
                         @Nonnull String username,
                         @Nonnull String password,
                         @Nonnull String passwordSubmit) {
        execute(authApi.register(csrf, username, password, passwordSubmit), 201);
    }

    @Step("Get register form")
    public void requestRegisterForm() {
        execute(authApi.requestRegisterForm(), 200);
    }
}
