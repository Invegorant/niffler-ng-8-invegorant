package guru.qa.niffler.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;

import javax.annotation.Nonnull;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AuthorityJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("authority")
        Authority authority,
        @JsonProperty("user")
        AuthUserJson user) {

    public static @Nonnull AuthorityJson fromJson(@Nonnull AuthorityEntity authority) {
        return new AuthorityJson(
                authority.getId(),
                authority.getAuthority(),
                AuthUserJson.fromEntity(authority.getUser())
        );
    }
}
