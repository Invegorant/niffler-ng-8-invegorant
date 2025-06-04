package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.UserJson;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@WebTest
@Feature("Друзья")
public class FriendsTest extends AbstractTest {

    @Test
    @User(friends = 1)
    @DisplayName("Друзья - Проверка наличия друга в списке друзей")
    void friends_friendShouldBePresentInFriendsTable(UserJson user) {
        openLoginPage()
                .doLogin(user.username(), user.testData().password())
                .openFriendsPage()
                .checkFriendIsPresentInTable(user.testData().friends().getFirst().username());
    }

    @Test
    @User
    @DisplayName("Друзья - Проверка пустого списка друзей")
    void friends_friendTableShouldBeEmptyForNewUser(UserJson user) {
        openLoginPage()
                .doLogin(user.username(), user.testData().password())
                .openFriendsPage()
                .checkFriendsTableIsEmpty();
    }

    @Test
    @User(incomeInvitations = 1)
    @DisplayName("Друзья - Проверка наличия входящего запроса в друзья от другого пользователя")
    void friends_incomeInvitationBePresentInFriendsTable(UserJson user) {
        openLoginPage()
                .doLogin(user.username(), user.testData().password())
                .openFriendsPage()
                .checkFriendRequestFromUser(user.testData().incomeInvitations().getFirst().username());
    }

    @Test
    @User(outcomeInvitations = 1)
    @DisplayName("Друзья - Проверка наличия исходящего запроса в друзья")
    void friends_outcomeInvitationBePresentInAllPeoplesTableTable(UserJson user) {
        openLoginPage()
                .doLogin(user.username(), user.testData().password())
                .openAllPeoplePage()
                .checkOutcomeRequestToUser(user.testData().outcomeInvitations().getFirst().username());
    }
}
