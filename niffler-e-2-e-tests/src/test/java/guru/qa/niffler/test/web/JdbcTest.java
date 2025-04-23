package guru.qa.niffler.test.web;

import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UserDbClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.test.web.AbstractTest.DEFAULT_USERNAME;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JdbcTest {

    private final UserDbClient userDbClient = new UserDbClient();

    @Test
    @DisplayName("Spring JDBC -> Создание УЗ с транзакцией")
    void springJdbcTxSuccessTest() {
        String username = RandomDataUtils.randomUsername();
        userDbClient.createUserSpringTx(
                new UserJson(
                        null,
                        username,
                        null,
                        null,
                        null,
                        CurrencyValues.RUB,
                        null,
                        null,
                        null
                )
        );
    }

    @Test
    @DisplayName("Spring JDBC -> Создание УЗ без транзакции")
    void springJdbcSuccessTest() {
        String username = RandomDataUtils.randomUsername();
        userDbClient.createUserSpringNoTx(
                new UserJson(
                        null,
                        username,
                        null,
                        null,
                        null,
                        CurrencyValues.RUB,
                        null,
                        null,
                        null
                )
        );
    }

    @Test
    @DisplayName("JDBC -> Создание УЗ с транзакцией")
    void jdbcTxSuccessTest() {
        String username = RandomDataUtils.randomUsername();
        userDbClient.createUserJdbcTx(
                new UserJson(
                        null,
                        username,
                        null,
                        null,
                        null,
                        CurrencyValues.RUB,
                        null,
                        null,
                        null
                )
        );
    }

    @Test
    @DisplayName("JDBC -> Создание УЗ без транзакции")
    void jdbcSuccessTest() {
        String username = RandomDataUtils.randomUsername();
        userDbClient.createUserJdbcNoTx(
                new UserJson(
                        null,
                        username,
                        null,
                        null,
                        null,
                        CurrencyValues.RUB,
                        null,
                        null,
                        null
                )
        );
    }

    @Test
    @DisplayName("Spring JDBC -> Создание УЗ с ChainedTransactionManager")
    void springChainedTxTest() {
        userDbClient.createUserSpringJdbcChainedTx(
                new UserJson(
                        null,
                        RandomDataUtils.randomUsername(),
                        null,
                        null,
                        null,
                        CurrencyValues.RUB,
                        null,
                        null,
                        null
                )
        );
    }

    @Test
    @DisplayName("JDBC -> Неуспешное создание УЗ")
    void failedXaTransactionTest() {
        String existedUsernameInDb = DEFAULT_USERNAME;
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userDbClient.createUserJdbcTx(
                        new UserJson(
                                null,
                                existedUsernameInDb, //уже есть в бд, будет ошибка
                                null,
                                null,
                                null,
                                CurrencyValues.RUB,
                                null,
                                null,
                                null
                        )
                ));
        assertTrue(exception.getMessage()
                .contains("Key (username)=(" + existedUsernameInDb + ") already exists."));
    }
}
