package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.MainPage;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@WebTest
@Feature("Друзья")
public class FriendsTest extends AbstractTest {

    @Test
    @ApiLogin
    @User(friends = 1)
    @DisplayName("Друзья - Проверка наличия друга в списке друзей")
    void friends_friendShouldBePresentInFriendsTable(UserJson user) {
        new MainPage()
                .getHeader()
                .toFriendsPage()
                .checkFriendIsPresentInTable(user.testData().friends().getFirst().username());
    }

    @Test
    @ApiLogin
    @User
    @DisplayName("Друзья - Проверка пустого списка друзей")
    void friends_friendTableShouldBeEmptyForNewUser() {
        new MainPage()
                .getHeader()
                .toFriendsPage()
                .checkFriendsTableIsEmpty();
    }

    @Test
    @ApiLogin
    @User(incomeInvitations = 1)
    @DisplayName("Друзья - Проверка наличия входящего запроса в друзья от другого пользователя")
    void friends_incomeInvitationBePresentInFriendsTable(UserJson user) {
        new MainPage()
                .getHeader()
                .toFriendsPage()
                .checkFriendRequestFromUser(user.testData().incomeInvitations().getFirst().username());
    }

    @Test
    @ApiLogin
    @User(outcomeInvitations = 1)
    @DisplayName("Друзья - Проверка наличия исходящего запроса в друзья")
    void friends_outcomeInvitationBePresentInAllPeoplesTableTable(UserJson user) {
        new MainPage()
                .getHeader()
                .toAllPeoplesPage()
                .searchRequestByUsername(user.username())
                .checkOutcomeRequestToUser(user.testData().outcomeInvitations().getFirst().username());
    }

    @Test
    @ApiLogin
    @User(incomeInvitations = 1)
    @DisplayName("Друзья - Проверка возможности принятия в друзья")
    void friends_userCanAcceptIncomeFriendInvitation(UserJson user) {
        String incomeUsername = user.testData().incomeInvitations().getFirst().username();
        new MainPage()
                .getHeader()
                .toFriendsPage()
                .acceptIncomingRequestFrom(incomeUsername)
                .checkFriendIsPresentInTable(incomeUsername);
    }

    @Test
    @ApiLogin
    @User(incomeInvitations = 1)
    @DisplayName("Друзья - Проверка возможности отказа принятия в друзья")
    void friends_userCanDeclineIncomeFriendInvitation(UserJson user) {
        String incomeUsername = user.testData().incomeInvitations().getFirst().username();
        new MainPage()
                .getHeader()
                .toFriendsPage()
                .declineIncomingRequestFrom(incomeUsername)
                .checkFriendsTableIsEmpty();
    }
}
