package guru.qa.niffler.data.dao.impl.spring;

import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.mapper.row_mapper.AuthUserEntityRowMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class AuthUserDaoSpringJdbc extends AbstractDaoSpring<AuthUserEntity> implements AuthUserDao {

    public AuthUserDaoSpringJdbc(DataSource dataSource) {
        super(dataSource, AuthUserEntityRowMapper.instance);
    }

    @NotNull
    @Override
    public AuthUserEntity create(AuthUserEntity user) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                            "VALUES (?,?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setBoolean(3, user.getEnabled());
            ps.setBoolean(4, user.getAccountNonExpired());
            ps.setBoolean(5, user.getAccountNonLocked());
            ps.setBoolean(6, user.getCredentialsNonExpired());
            return ps;
        }, kh);

        final UUID generatedKey = (UUID) Objects.requireNonNull(kh.getKeys()).get("id");
        user.setId(generatedKey);
        return user;
    }

    @NotNull
    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        "SELECT * FROM \"user\" WHERE id = ?",
                        rowMapper,
                        id
                )
        );
    }

    @NotNull
    @Override
    public Optional<AuthUserEntity> findByUsername(String username) {
        AuthUserEntity entity = jdbcTemplate.queryForObject(
                "SELECT * FROM \"user\" WHERE username = ?",
                rowMapper,
                username);
        return Optional.ofNullable(entity);
    }

    @NotNull
    @Override
    public List<AuthUserEntity> findAll() {
        return jdbcTemplate.query(
                "SELECT * FROM \"user\"",
                rowMapper
        );
    }

    @Override
    public boolean delete(AuthUserEntity authUser) {
        int rowsAffected = jdbcTemplate.update("DELETE FROM \"user\" WHERE id = ?", authUser.getId());
        return rowsAffected > 0;
    }
}
