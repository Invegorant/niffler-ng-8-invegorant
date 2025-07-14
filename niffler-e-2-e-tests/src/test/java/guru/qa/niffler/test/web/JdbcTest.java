package guru.qa.niffler.test.web;

import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.impl.UsersDbClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static guru.qa.niffler.test.web.AbstractTest.DEFAULT_PASSWORD;
import static guru.qa.niffler.test.web.AbstractTest.DEFAULT_USERNAME;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
public class JdbcTest {

    private final UsersDbClient usersDbClient = new UsersDbClient();

    @Test
    @DisplayName("Spring JDBC -> Создание УЗ с транзакцией")
    void springJdbcTxSuccessTest() {
        String username = RandomDataUtils.randomUsername();
        usersDbClient.createUserSpringTx(
                new UserJson(
                        null,
                        username,
                        null,
                        null,
                        null,
                        CurrencyValues.RUB,
                        null,
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
        usersDbClient.createUserSpringNoTx(
                new UserJson(
                        null,
                        username,
                        null,
                        null,
                        null,
                        CurrencyValues.RUB,
                        null,
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
        usersDbClient.createUserJdbcTx(
                new UserJson(
                        null,
                        username,
                        null,
                        null,
                        null,
                        CurrencyValues.RUB,
                        null,
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
        usersDbClient.createUserJdbcNoTx(
                new UserJson(
                        null,
                        username,
                        null,
                        null,
                        null,
                        CurrencyValues.RUB,
                        null,
                        null,
                        null,
                        null
                )
        );
    }

    @Test
    @DisplayName("Spring JDBC -> Создание УЗ с ChainedTransactionManager")
    void springChainedTxTest() {
        usersDbClient.createUserSpringJdbcChainedTx(
                new UserJson(
                        null,
                        RandomDataUtils.randomUsername(),
                        null,
                        null,
                        null,
                        CurrencyValues.RUB,
                        null,
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
                () -> usersDbClient.createUserJdbcTx(
                        new UserJson(
                                null,
                                existedUsernameInDb, //уже есть в бд, будет ошибка
                                null,
                                null,
                                null,
                                CurrencyValues.RUB,
                                null,
                                null,
                                null,
                                null
                        )
                ));
        assertTrue(exception.getMessage()
                .contains("Key (username)=(" + existedUsernameInDb + ") already exists."));
    }

    @ValueSource(strings = {
            "valentin-10"
    })
    @ParameterizedTest
    void hibernateCreateUserSuccessTest(String uname) {

        UserJson user = usersDbClient.createUser(
                uname,
                "12345"
        );

        usersDbClient.createIncomeInvitations(user, 1);
        usersDbClient.createOutcomeInvitations(user, 1);
    }

    @Test
    @DisplayName("Hibernate -> UsersClient - Income invitation")
    void hibernateUsersClientCreateIncomeInvitationsSuccessTest() {
        UserJson user = usersDbClient.createUser(RandomDataUtils.randomUsername(), DEFAULT_PASSWORD);
        usersDbClient.createIncomeInvitations(user, RandomDataUtils.randomCount());
    }

    @Test
    @DisplayName("Hibernate -> UsersClient - Outcome invitation")
    void hibernateUsersClientCreateOutcomeInvitationsSuccessTest() {
        UserJson user = usersDbClient.createUser(RandomDataUtils.randomUsername(), DEFAULT_PASSWORD);
        usersDbClient.createOutcomeInvitations(user, RandomDataUtils.randomCount());
    }

    @Test
    @DisplayName("Hibernate -> UsersClient - Create friends")
    void hibernateUsersClientCreateFriendsSuccessTest() {
        UserJson user = usersDbClient.createUser(RandomDataUtils.randomUsername(), DEFAULT_PASSWORD);
        usersDbClient.createFriends(user, RandomDataUtils.randomCount());
    }

    @Test
    @DisplayName("Hibernate -> UsersClient - Remove")
    void hibernateUsersClientRemoveSuccessTest() {
        UserJson user = usersDbClient.createUser(RandomDataUtils.randomUsername(), DEFAULT_PASSWORD);
        usersDbClient.createFriends(user, RandomDataUtils.randomCount());
        usersDbClient.remove(user);
    }
}
