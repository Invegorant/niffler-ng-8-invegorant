package guru.qa.niffler.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.core.CodeInterceptor;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.test.web.utils.OauthUtils;
import lombok.SneakyThrows;
import retrofit2.Response;

import javax.annotation.Nonnull;

public class AuthApiClient extends RestClient {

    private final AuthApi authApi;

    public AuthApiClient() {
        super(CFG.authUrl(), true, new CodeInterceptor());
        authApi = create(AuthApi.class);
    }

    @SneakyThrows
    public String login(@Nonnull String username,
                        @Nonnull String password) {
        final String codeVerifier = OauthUtils.generateCodeVerifier();
        final String codeChallenge = OauthUtils.generateCodeChallenge(codeVerifier);
        final String redirectUri = CFG.frontUrl() + "authorized";
        final String clientId = "client";

        execute(authApi.authorize(
                "code",
                clientId,
                "openid",
                redirectUri,
                codeChallenge,
                "S256"
        ));

        Response<Void> loginResponse =
                authApi.login(
                        ThreadSafeCookieStore.INSTANCE.getCookieValue("XSRF-TOKEN"),
                        username,
                        password
                ).execute();

        JsonNode tokenResponses = execute(authApi.token(
                ApiLoginExtension.getCode(),
                redirectUri,
                codeVerifier,
                "authorization_code",
                clientId

        ), 200);
        return tokenResponses.get("id_token").asText();
    }

    public void requestRegisterForm() {
        execute(authApi.requestRegisterForm());
    }

    public void register(String username, String password, String passwordSubmit, String cookieValue) {
        execute(authApi.register(username, password, passwordSubmit, cookieValue), 201);
    }
}
