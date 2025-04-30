package guru.qa.niffler.data.entity.auth;

import guru.qa.niffler.model.AuthUserJson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthUserEntity implements Serializable {
    private UUID id;
    private String username;
    private String password;
    private Boolean enabled;
    private Boolean accountNonExpired;
    private Boolean accountNonLocked;
    private Boolean credentialsNonExpired;

    public static @Nonnull AuthUserEntity fromJson(@Nonnull AuthUserJson user) {
        return new AuthUserEntity(
                user.id(),
                user.username(),
                user.password(),
                user.enabled(),
                user.accountNonExpired(),
                user.accountNonLocked(),
                user.credentialsNonExpired()
        );
    }
}
