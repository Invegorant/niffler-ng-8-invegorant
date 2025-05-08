package guru.qa.niffler.data.repository.impl.spring;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.mapper.extractor.AuthUserEntityExtractor;
import guru.qa.niffler.data.repository.AuthUserRepository;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.DataSources.dataSource;

public class AuthUserRepositorySpringJdbc extends AbstractRepSpring<AuthUserEntity> implements AuthUserRepository {

    public AuthUserRepositorySpringJdbc() {
        super(dataSource(Config.getInstance().authJdbcUrl()), AuthUserEntityExtractor.INSTANCE);
    }

    @Override
    public AuthUserEntity create(AuthUserEntity user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO \"user\" (username, password, enabled, account_non_expired, " +
                            "account_non_locked, credentials_non_expired) VALUES (?, ?, ?, ?, ?, ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setBoolean(3, user.getEnabled());
            ps.setBoolean(4, user.getAccountNonExpired());
            ps.setBoolean(5, user.getAccountNonLocked());
            ps.setBoolean(6, user.getCredentialsNonExpired());
            return ps;
        }, keyHolder);
        final UUID generatedKey = (UUID) keyHolder.getKeys().get("id");
        user.setId(generatedKey);
        jdbcTemplate.batchUpdate(
                "INSERT INTO \"authority\" (user_id, authority) VALUES (?, ?)",
                user.getAuthorities(),
                user.getAuthorities().size(),
                (ps, authority) -> {
                    ps.setObject(1, generatedKey);
                    ps.setString(2, authority.getAuthority().name());
                });

        return user;
    }

    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        return Optional.of(Objects.requireNonNull(jdbcTemplate.query(
                """
                        SELECT a.id as authority_id,
                         authority,
                         user_id as id,
                         u.username,
                         u.password,
                         u.enabled,
                         u.account_non_expired,
                         u.account_non_locked,
                         u.credentials_non_expired
                        FROM "user" u join public.authority a on u.id = a.user_id WHERE u.id = ?""",
                AuthUserEntityExtractor.INSTANCE,
                id
        )));
    }
}
