package guru.qa.niffler.test.rest;

import guru.qa.niffler.jupiter.annotation.meta.RestTest;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.impl.UsersApiClient;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

@RestTest
@Order(Integer.MAX_VALUE)
public class PopulatedUsersListTest {

    @User
    @Test
    void verifyAllUsersResponseIsNotEmpty(UserJson user) {
        final UsersApiClient client = new UsersApiClient();
        List<UserJson> users = client.allUsers(user.username(), "");
        assertFalse(users.isEmpty());
    }
}