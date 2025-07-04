package guru.qa.niffler.data.repository.impl.jdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.mapper.row_mapper.AuthUserEntityRowMapper;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.model.Authority;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jdbc.Connections.holder;

@ParametersAreNonnullByDefault
public class AuthUserRepositoryJdbc implements AuthUserRepository {

    private static final Config CFG = Config.getInstance();
    private static final String URL = CFG.authJdbcUrl();

    @SuppressWarnings("resource")
    @NotNull
    @Override
    public AuthUserEntity create(@NotNull AuthUserEntity user) {
        try (PreparedStatement userPs = holder(URL).connection().prepareStatement(
                "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                        "VALUES (?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
             PreparedStatement authorityPs = holder(URL).connection().prepareStatement(
                     "INSERT INTO \"authority\" (user_id, authority) VALUES (?, ?)")) {
            userPs.setString(1, user.getUsername());
            userPs.setString(2, user.getPassword());
            userPs.setBoolean(3, user.getEnabled());
            userPs.setBoolean(4, user.getAccountNonExpired());
            userPs.setBoolean(5, user.getAccountNonLocked());
            userPs.setBoolean(6, user.getCredentialsNonExpired());

            userPs.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = userPs.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can`t find id in ResultSet");
                }
            }
            user.setId(generatedKey);

            for (AuthorityEntity a : user.getAuthorities()) {
                authorityPs.setObject(1, generatedKey);
                authorityPs.setString(2, a.getAuthority().name());
                authorityPs.addBatch();
                authorityPs.clearParameters();
            }
            authorityPs.executeBatch();
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("resource")
    @NotNull
    @Override
    public AuthUserEntity update(@NotNull AuthUserEntity user) {
        try (PreparedStatement ps = holder(URL).connection().prepareStatement(
                "UPDATE \"user\" SET " +
                        "username = ?," +
                        "password = ?," +
                        "enabled = ?," +
                        "account_non_expired = ?," +
                        "account_non_locked = ?," +
                        "credentials_non_expired = ?" +
                        "WHERE id = ?"
        )) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setBoolean(3, user.getEnabled());
            ps.setBoolean(4, user.getAccountNonExpired());
            ps.setBoolean(5, user.getAccountNonLocked());
            ps.setBoolean(6, user.getCredentialsNonExpired());
            ps.setObject(7, user.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    @SuppressWarnings("resource")
    @NotNull
    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        try (PreparedStatement ps = holder(URL).connection().prepareStatement(
                "select * from \"user\" u join authority a on u.id = a.user_id where u.id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();

            return getAuthUserEntityFromResultSet(ps);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("resource")
    @NotNull
    @Override
    public Optional<AuthUserEntity> findByUsername(String username) {
        try (PreparedStatement ps = holder(URL).connection().prepareStatement(
                "select * from \"user\" u join authority a on u.id = a.user_id where u.username = ?"
        )) {
            ps.setString(1, username);
            ps.execute();

            return getAuthUserEntityFromResultSet(ps);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("resource")
    @Override
    public void remove(@NotNull AuthUserEntity user) {
        try (PreparedStatement ps = holder(URL).connection().prepareStatement(
                "DELETE FROM \"user\" WHERE id = ?")) {
            ps.setObject(1, user.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<AuthUserEntity> getAuthUserEntityFromResultSet(PreparedStatement ps) throws SQLException {
        try (ResultSet rs = ps.getResultSet()) {
            AuthUserEntity user = null;
            List<AuthorityEntity> authorityEntities = new ArrayList<>();
            while (rs.next()) {
                if (user == null) {
                    user = AuthUserEntityRowMapper.instance.mapRow(rs, 1);
                }
                AuthorityEntity ae = new AuthorityEntity();
                ae.setUser(user);
                ae.setId(rs.getObject("a.id", UUID.class));
                ae.setAuthority(Authority.valueOf(rs.getString("authority")));
                authorityEntities.add(ae);
            }
            if (user == null) {
                return Optional.empty();
            } else {
                user.setAuthorities(authorityEntities);
                return Optional.of(user);
            }
        }
    }
}
