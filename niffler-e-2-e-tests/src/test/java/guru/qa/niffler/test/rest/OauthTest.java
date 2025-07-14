package guru.qa.niffler.test.rest;

import guru.qa.niffler.jupiter.annotation.meta.RestTest;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.impl.AuthApiClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@RestTest
public class OauthTest {

    private final AuthApiClient authApiClient = new AuthApiClient();

    @User
    @Test
    void oauthTest(UserJson userJson) {
        String token = authApiClient.login(userJson.username(), userJson.testData().password());
        assertNotNull(token);
    }
}
