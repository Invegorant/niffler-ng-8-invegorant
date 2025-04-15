package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.extension.UsersQueueExtension;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.StaticUser;
import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType;
import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType.Type.*;

@Feature("Друзья")
public class FriendsTest extends AbstractTest {

    @Test
    @DisplayName("Друзья - Проверка наличия друга в списке друзей")
    @ExtendWith(UsersQueueExtension.class)
    void friends_friendShouldBePresentInFriendsTable(@UserType(WITH_FRIEND) StaticUser user) {
        openLoginPage()
                .doLogin(user.username(), user.password())
                .openFriendsPage()
                .checkFriendIsPresentInTable(user.friend());
    }

    @Test
    @DisplayName("Друзья - Проверка пустого списка друзей")
    @ExtendWith(UsersQueueExtension.class)
    void friends_friendTableShouldBeEmptyForNewUser(@UserType(EMPTY) StaticUser user) {
        openLoginPage()
                .doLogin(user.username(), user.password())
                .openFriendsPage()
                .checkFriendsTableIsEmpty();
    }

    @Test
    @DisplayName("Друзья - Проверка наличия входящего запроса в друзья от другого пользователя")
    @ExtendWith(UsersQueueExtension.class)
    void friends_incomeInvitationBePresentInFriendsTable(@UserType(WITH_INCOME_REQUEST) StaticUser user) {
        openLoginPage()
                .doLogin(user.username(), user.password())
                .openFriendsPage()
                .checkFriendRequestFromUser(user.income());
    }

    @Test
    @DisplayName("Друзья - Проверка наличия исходящего запроса в друзья")
    @ExtendWith(UsersQueueExtension.class)
    void friends_outcomeInvitationBePresentInAllPeoplesTableTable(@UserType(WITH_OUTCOME_REQUEST) StaticUser user) {
        openLoginPage()
                .doLogin(user.username(), user.password())
                .openAllPeoplePage()
                .checkOutcomeRequestToUser(user.outcome());
    }
}
