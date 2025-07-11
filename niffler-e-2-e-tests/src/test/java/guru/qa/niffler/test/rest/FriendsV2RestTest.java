package guru.qa.niffler.test.rest;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.model.pageable.RestResponsePage;
import guru.qa.niffler.model.rest.UserJson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

public class FriendsV2RestTest extends AbstractRestTest {

    @User(friends = 1, incomeInvitations = 2)
    @ApiLogin
    @Test
    void friendsAndIncomeInvitationsShouldBeReturnedFromGateway(@Token String bearerToken) {
        final Page<UserJson> responseBody = gwV2ApiClient.allFriends(
                bearerToken,
                0,
                10,
                null
        );
        Assertions.assertEquals(3, responseBody.getContent().size());
    }


    @User(friends = 12)
    @ApiLogin
    @Test
    void shouldSupportPaginationForFriendsList(UserJson user, @Token String userToken) {
        final int page = 0;
        final int size = 5;

        RestResponsePage<UserJson> pagedResponse = gwV2ApiClient.allFriends(userToken, page, size, user.username());

        assertSoftly(softly -> {
            softly.assertThat(pagedResponse.getContent()).hasSize(size);
            softly.assertThat(pagedResponse.getPageable().getPageNumber()).isEqualTo(page);
            softly.assertThat(pagedResponse.getPageable().getPageSize()).isEqualTo(size);
            softly.assertThat(pagedResponse.getTotalElements()).isEqualTo(user.testData().friends().size());
            softly.assertThat(pagedResponse.getTotalPages()).isEqualTo(3);
        });
    }
}