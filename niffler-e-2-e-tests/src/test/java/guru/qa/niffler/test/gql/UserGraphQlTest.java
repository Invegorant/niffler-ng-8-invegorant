package guru.qa.niffler.test.gql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.apollo.api.Error;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.FriendsWithCategoriesQuery;
import guru.qa.NestedFriends2LevelQuery;
import guru.qa.NestedFriends3LevelQuery;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.meta.User;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserGraphQlTest extends BaseGraphQlTest {

    @Test
    @ApiLogin
    @User(friends = 1)
    void categoriesForFriendsShouldReturnError(@Token String bearerToken) {
        final ApolloCall<FriendsWithCategoriesQuery.Data> friendsWithCategoriesCall =
                apolloClient.query(FriendsWithCategoriesQuery.builder()
                                .page(0)
                                .size(10)
                                .sort(null)
                                .build())
                        .addHttpHeader("authorization", bearerToken);

        final ApolloResponse<FriendsWithCategoriesQuery.Data> response =
                Rx2Apollo.single(friendsWithCategoriesCall).blockingGet();
        Error error = Objects.requireNonNull(response.errors).getFirst();


        assertEquals("Can`t query categories for another user", error.getMessage());
    }

    @Test
    @ApiLogin
    @User(friends = 1)
    void nestedFriendsShouldReturnError(@Token String bearerToken) {
        final ApolloCall<NestedFriends3LevelQuery.Data> nestedFriendsCall =
                apolloClient.query(NestedFriends3LevelQuery.builder()
                                .page(0)
                                .size(10)
                                .build())
                        .addHttpHeader("authorization", bearerToken);

        final ApolloResponse<NestedFriends3LevelQuery.Data> response =
                Rx2Apollo.single(nestedFriendsCall).blockingGet();
        Error error = Objects.requireNonNull(response.errors).getFirst();


        assertEquals("Can`t fetch over 2 friends sub-queries", error.getMessage());
    }

    @Test
    @ApiLogin
    @User(friends = 1)
    void nestedFriendsShouldBeForbidden(@Token String bearerToken) {
        final ApolloCall<NestedFriends2LevelQuery.Data> nestedFriendsCall =
                apolloClient.query(NestedFriends2LevelQuery.builder()
                                .page(0)
                                .size(10)
                                .build()
                        )
                        .addHttpHeader("authorization", bearerToken);

        final ApolloResponse<NestedFriends2LevelQuery.Data> response =
                Rx2Apollo.single(nestedFriendsCall).blockingGet();
        Error error = Objects.requireNonNull(response.errors).getFirst();

        assertEquals("Nested friends queries are forbidden", error.getMessage());
    }
}
