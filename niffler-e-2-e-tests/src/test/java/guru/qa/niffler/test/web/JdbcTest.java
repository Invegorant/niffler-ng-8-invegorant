package guru.qa.niffler.test.web;

import guru.qa.niffler.model.AuthUserJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.AuthDbClient;
import guru.qa.niffler.service.UserDbClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.test.web.AbstractTest.DEFAULT_PASSWORD;
import static guru.qa.niffler.test.web.AbstractTest.DEFAULT_USERNAME;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JdbcTest {

    @Test
    void successfulTransactionTest() {
        AuthDbClient authDbClient = new AuthDbClient();
        authDbClient.createUser(
                new AuthUserJson(
                        null,
                        RandomDataUtils.randomUsername(),
                        DEFAULT_PASSWORD,
                        true,
                        true,
                        true,
                        true
                )
        );
    }

    @Test
    void successfulXaTransactionTest() {
        UserDbClient userDbClient = new UserDbClient();
        String username = RandomDataUtils.randomUsername();
        userDbClient.createUser(
                new AuthUserJson(
                        null,
                        username,
                        DEFAULT_PASSWORD,
                        true,
                        true,
                        true,
                        true
                ),
                new UserJson(
                        null,
                        username,
                        null,
                        null,
                        null,
                        CurrencyValues.RUB,
                        null,
                        null
                )
        );
    }

    @Test
    void failedXaTransactionTest() {
        UserDbClient userDbClient = new UserDbClient();
        String existedUsernameInDb = DEFAULT_USERNAME;
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userDbClient.createUser(
                        new AuthUserJson(
                                null,
                                RandomDataUtils.randomUsername(),
                                DEFAULT_PASSWORD,
                                true,
                                true,
                                true,
                                true
                        ),
                        new UserJson(
                                null,
                                existedUsernameInDb, //уже есть в бд, будет ошибка
                                null,
                                null,
                                null,
                                CurrencyValues.RUB,
                                null,
                                null
                        )
                ));
        assertTrue(exception.getMessage()
                .contains("Key (username)=(" + existedUsernameInDb + ") already exists."));
    }

    @Test
    void springJdbcTest() {
        UserDbClient usersDbClient = new UserDbClient();
        UserJson user = usersDbClient.createUserSpringJdbc(
                new UserJson(
                        null,
                        RandomDataUtils.randomUsername(),
                        RandomDataUtils.randomName(),
                        RandomDataUtils.randomSurname(),
                        null,
                        CurrencyValues.RUB,
                        null,
                        null
                )
        );
        System.out.println(user);
    }
}
