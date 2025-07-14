package guru.qa.niffler.test.rest;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static guru.qa.niffler.model.FriendshipStatus.*;
import static guru.qa.niffler.test.web.AbstractTest.DEFAULT_PASSWORD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

public class FriendsRestTest extends AbstractRestTest {

    @User(friends = 1, incomeInvitations = 2)
    @ApiLogin
    @Test
    void friendsAndIncomeInvitationsShouldBeReturnedFromGateway(@Token String bearerToken) {
        final List<UserJson> responseBody = gwApiClient.allFriends(bearerToken, null);
        Assertions.assertEquals(3, responseBody.size());
    }

    @User(friends = 3, incomeInvitations = 3, outcomeInvitations = 3)
    @ApiLogin
    @Test
    void shouldReturnFriendWhenSearchingByUsername(UserJson user, @Token String userToken) {
        String existingFriendUsername = user.testData().friends().getFirst().username();
        List<UserJson> searchResult = gwApiClient.allFriends(userToken, existingFriendUsername);
        assertSoftly(softly -> {
            softly.assertThat(searchResult).hasSize(1);
            softly.assertThat(searchResult.getFirst().friendshipStatus()).isEqualTo(FRIEND);
            softly.assertThat(searchResult.getFirst().username()).isEqualTo(existingFriendUsername);
        });
    }

    @User(friends = 3, incomeInvitations = 3, outcomeInvitations = 3)
    @ApiLogin
    @Test
    void shouldReturnReceivedInvitationWhenSearchingByUsername(UserJson user, @Token String userToken) {
        String senderUsername = user.testData().incomeInvitations().getFirst().username();
        List<UserJson> searchResult = gwApiClient.allFriends(userToken, senderUsername);
        assertSoftly(softly -> {
            softly.assertThat(searchResult).hasSize(1);
            softly.assertThat(searchResult.getFirst().friendshipStatus()).isEqualTo(INVITE_RECEIVED);
            softly.assertThat(searchResult.getFirst().username()).isEqualTo(senderUsername);
        });
    }

    @User(friends = 3, incomeInvitations = 3, outcomeInvitations = 3)
    @ApiLogin
    @Test
    void shouldReturnSentInvitationWhenSearchingByUsername(UserJson user, @Token String userToken) {
        String invitedUsername = user.testData().outcomeInvitations().getFirst().username();
        List<UserJson> searchResult = gwApiClient.allUsers(userToken, invitedUsername);
        assertSoftly(softly -> {
            softly.assertThat(searchResult).hasSize(1);
            softly.assertThat(searchResult.getFirst().friendshipStatus()).isEqualTo(INVITE_SENT);
            softly.assertThat(searchResult.getFirst().username()).isEqualTo(invitedUsername);
        });
    }

    @User(friends = 3, incomeInvitations = 3)
    @ApiLogin
    @Test
    void shouldReturnEmptyListWhenSearchQueryDoesNotMatchAnyFriend(UserJson user, @Token String userToken) {
        List<UserJson> searchResult = gwApiClient.allFriends(userToken, RandomDataUtils.randomName());
        assertThat(searchResult).isEmpty();
    }

    @User(outcomeInvitations = 3)
    @ApiLogin
    @Test
    void shouldReturnEmptyListWhenSearchQueryDoesNotMatchAnyInvitationSent(UserJson user, @Token String userToken) {
        List<UserJson> searchResult = gwApiClient.getOutcomeInvitations(userToken, RandomDataUtils.randomName());
        assertThat(searchResult).isEmpty();
    }

    @User(friends = 1)
    @ApiLogin
    @Test
    void shouldRemoveConfirmedFriendSuccessfully(UserJson user, @Token String userToken) {
        String friendUsername = user.testData().friends().getFirst().username();
        List<UserJson> friendsBeforeRemoval = gwApiClient.getConfirmedFriends(userToken, null);
        assertThat(friendsBeforeRemoval).hasSize(1);

        gwApiClient.removeFriend(userToken, friendUsername);

        List<UserJson> friendsAfterRemoval = gwApiClient.allFriends(userToken, null);
        UserJson friendAfterRemoval = gwApiClient.allUsers(userToken, friendUsername).getFirst();

        assertSoftly(softly -> {
            softly.assertThat(friendsAfterRemoval).isEmpty();
            softly.assertThat(friendAfterRemoval.friendshipStatus()).isNull();
        });
    }

    @User(incomeInvitations = 1)
    @ApiLogin
    @Test
    void shouldUpdateFriendshipStatusAfterConfirmingFriendship(UserJson recipient, @Token String recipientToken) {
        List<UserJson> friendsBeforeAcceptance = gwApiClient.getConfirmedFriends(recipientToken, null);
        assertThat(friendsBeforeAcceptance).isEmpty();

        UserJson sender = recipient.testData().incomeInvitations().getFirst();
        gwApiClient.acceptInvitation(recipientToken, sender);

        List<UserJson> friendsAfterAcceptance = gwApiClient.getConfirmedFriends(recipientToken, null);
        assertSoftly(softly -> {
            softly.assertThat(friendsAfterAcceptance).hasSize(1);
            softly.assertThat(friendsAfterAcceptance.getFirst().username()).isEqualTo(sender.username());
            softly.assertThat(friendsAfterAcceptance.getFirst().friendshipStatus()).isEqualTo(FRIEND);
        });
    }

    @User
    @ApiLogin
    @Test
    void shouldUpdateFriendshipStatusAfterSendingInvitation(@Token String senderToken) {
        List<UserJson> outcomeInvitations = gwApiClient.getOutcomeInvitations(senderToken, null);
        assertThat(outcomeInvitations).isEmpty();

        String recipientUsername = RandomDataUtils.randomName();
        UserJson recipient = userApiClient.createUser(recipientUsername, DEFAULT_PASSWORD);
        gwApiClient.sendInvitation(senderToken, recipient);

        List<UserJson> outcomeInvitationsAfterSending = gwApiClient.getOutcomeInvitations(senderToken, null);
        assertSoftly(softly -> {
            softly.assertThat(outcomeInvitationsAfterSending).hasSize(1);
            softly.assertThat(outcomeInvitationsAfterSending.getFirst().username()).isEqualTo(recipient.username());
        });
    }

    @User(incomeInvitations = 1)
    @ApiLogin
    @Test
    void shouldUpdateFriendshipStatusIfInvitationDeclined(@Token String token, UserJson user) {
        List<UserJson> incomeInvitations = gwApiClient.getIncomeInvitations(token, null);

        assertThat(incomeInvitations.size()).isEqualTo(1);

        UserJson sender = user.testData().incomeInvitations().getFirst();
        gwApiClient.declineInvitation(token, sender);

        List<UserJson> incomeInvitationsAfterDecline = gwApiClient.getIncomeInvitations(token, null);
        UserJson senderAfterDecline = gwApiClient.allUsers(token, sender.username()).getFirst();

        assertSoftly(softly -> {
            softly.assertThat(incomeInvitationsAfterDecline).isEmpty();
            softly.assertThat(senderAfterDecline.friendshipStatus()).isNull();
        });
    }
}
