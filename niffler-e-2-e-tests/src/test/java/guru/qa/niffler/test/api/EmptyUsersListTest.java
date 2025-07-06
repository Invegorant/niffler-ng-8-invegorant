package guru.qa.niffler.test.api;

import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.impl.UsersApiClient;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Order(1)
public class EmptyUsersListTest {

    @User
    @Test
    void verifyAllUsersResponseIsEmpty(UserJson user) {
        final UsersApiClient client = new UsersApiClient();
        List<UserJson> users = client.allUsers(user.username(), "");
        assertTrue(users.isEmpty());
    }
}