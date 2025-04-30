package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;

public class AuthUserDaoJdbc implements AuthUserDao {

    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    private final Connection connection;

    public AuthUserDaoJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public AuthUserEntity create(AuthUserEntity authUser) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                        "VALUES (?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, authUser.getUsername());
            ps.setString(2, pe.encode(authUser.getPassword()));
            ps.setBoolean(3, authUser.getEnabled());
            ps.setBoolean(4, authUser.getAccountNonExpired());
            ps.setBoolean(5, authUser.getAccountNonLocked());
            ps.setBoolean(6, authUser.getCredentialsNonExpired());

            ps.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can`t find id in ResultSet");
                }
            }
            authUser.setId(generatedKey);
            return authUser;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM \"user\" WHERE id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                return rs.next() ? Optional.of(convertResultSetToAuthUserEntity(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<AuthUserEntity> findByUsername(String username) {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM \"user\" WHERE username = ?"
        )) {
            ps.setObject(1, username);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                return rs.next() ? Optional.of(convertResultSetToAuthUserEntity(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(AuthUserEntity authUser) {
        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM \"user\" WHERE id = ?")) {
            ps.setObject(1, authUser.getId());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private AuthUserEntity convertResultSetToAuthUserEntity(ResultSet rs) {
        try {
            return new AuthUserEntity(
                    rs.getObject("id", UUID.class),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getBoolean("enabled"),
                    rs.getBoolean("account_non_expired"),
                    rs.getBoolean("account_non_locked"),
                    rs.getBoolean("credentials_non_expired")
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
