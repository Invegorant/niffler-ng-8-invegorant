package guru.qa.niffler.test.grpc;


import guru.qa.niffler.grpc.*;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.model.rest.UserJson;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.model.FriendshipStatus.INVITE_SENT;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Feature("GRPC -> Userdata")
public class UserdataGrpcTest extends BaseGrpcTest {

    @Test
    @User(friends = 3)
    @DisplayName("GRPC -> Userdata - Получение списка друзей")
    void grpc_shouldReturnFriendsListPage(UserJson user) {
        final UserPageRequest request = UserPageRequest.newBuilder()
                .setUsername(user.username())
                .setPage(0)
                .setSize(10)
                .build();
        UserPageResponse userPageResponse = USERDATA_BLOCKING_STUB.friendsPage(request);

        assertSoftly(softly -> {
            softly.assertThat(userPageResponse.getEdgesCount()).isEqualTo(3);
            softly.assertThat(userPageResponse.getTotalElements()).isEqualTo(3);
            softly.assertThat(userPageResponse.getTotalPages()).isEqualTo(1);
        });
    }

    @Test
    @User(friends = 5)
    @DisplayName("GRPC -> Userdata - Получение списка друзей по username")
    void grpc_shouldReturnFriendFilteredByUsername(UserJson user) {
        final String expectedUsername = user.testData().friendsUsernames().getFirst();
        final UserPageRequest request = UserPageRequest.newBuilder()
                .setUsername(user.username())
                .setPage(0)
                .setSize(10)
                .setSearchQuery(expectedUsername)
                .build();
        UserPageResponse userPageResponse = USERDATA_BLOCKING_STUB.friendsPage(request);

        assertSoftly(softly -> {
            softly.assertThat(userPageResponse.getEdgesList().getFirst().getUsername()).isEqualTo(expectedUsername);
            softly.assertThat(userPageResponse.getEdgesCount()).isEqualTo(1);
        });
    }

    @Test
    @User(friends = 1)
    @DisplayName("GRPC -> Userdata - Удаление друга")
    void grpc_shouldRemoveFriendship(UserJson user) {
        final String friendToRemove = user.testData().friendsUsernames().getFirst();
        final FriendshipRequest removeRequest = FriendshipRequest.newBuilder()
                .setUsername(user.username())
                .setTargetUsername(friendToRemove)
                .build();
        USERDATA_BLOCKING_STUB.removeFriend(removeRequest);

        final UserBulkRequest request = UserBulkRequest.newBuilder()
                .setUsername(user.username())
                .setSearchQuery(friendToRemove)
                .build();
        UsersBulkResponse userPageResponse = USERDATA_BLOCKING_STUB.friends(request);

        assertEquals(0, userPageResponse.getUserForBulkResponseList().size());
    }

    @Test
    @User(incomeInvitations = 1)
    @DisplayName("GRPC -> Userdata - Принятие FriendshipRequest")
    void grpc_shouldAcceptFriendship(UserJson user) {
        final UserJson friendToAccept = user.testData().incomeInvitations().getFirst();
        final FriendshipRequest removeRequest = FriendshipRequest.newBuilder()
                .setUsername(user.username())
                .setTargetUsername(friendToAccept.username())
                .build();
        USERDATA_BLOCKING_STUB.acceptFriendshipRequest(removeRequest);

        final UserBulkRequest request = UserBulkRequest.newBuilder()
                .setUsername(user.username())
                .setSearchQuery(friendToAccept.username())
                .build();
        UsersBulkResponse userPageResponse = USERDATA_BLOCKING_STUB.friends(request);

        assertEquals(
                friendToAccept.username(),
                userPageResponse.getUserForBulkResponseList().getFirst().getUsername()
        );
    }

    @Test
    @User(incomeInvitations = 1)
    @DisplayName("GRPC -> Userdata - Отклонение FriendshipRequest")
    void grpc_shouldDeclineFriendship(UserJson user) {
        final UserJson friendToDecline = user.testData().incomeInvitations().getFirst();
        final FriendshipRequest removeRequest = FriendshipRequest.newBuilder()
                .setUsername(user.username())
                .setTargetUsername(friendToDecline.username())
                .build();
        USERDATA_BLOCKING_STUB.declineFriendshipRequest(removeRequest);

        final UserBulkRequest request = UserBulkRequest.newBuilder()
                .setUsername(user.username())
                .setSearchQuery(friendToDecline.username())
                .build();
        UsersBulkResponse userPageResponse = USERDATA_BLOCKING_STUB.friends(request);

        assertEquals(0, userPageResponse.getUserForBulkResponseList().size());
    }

    @Test
    @User
    @DisplayName("GRPC -> Userdata - Отправка FriendshipRequest")
    void grpc_sendFriendshipRequest(UserJson user) {
        final String username = user.username();
        final UserPageRequest allUsersRequest = UserPageRequest.newBuilder()
                .setUsername(username)
                .setPage(2)
                .setSize(1)
                .build();

        String targetUsername = USERDATA_BLOCKING_STUB.allUsersPage(allUsersRequest)
                .getEdgesList()
                .getLast()
                .getUsername();

        final FriendshipRequest friendshipRequest = FriendshipRequest.newBuilder()
                .setUsername(username)
                .setTargetUsername(targetUsername)
                .build();
        UserResponse targetUserResponse = USERDATA_BLOCKING_STUB.createFriendshipRequest(friendshipRequest);

        assertSoftly(softly -> {
            softly.assertThat(targetUserResponse.getUsername()).isEqualTo(username);
            softly.assertThat(targetUserResponse.getFriendshipStatus().name()).isEqualTo(INVITE_SENT.name());
        });
    }
}
