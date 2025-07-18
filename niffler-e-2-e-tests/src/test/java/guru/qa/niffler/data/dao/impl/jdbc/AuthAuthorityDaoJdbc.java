package guru.qa.niffler.data.dao.impl.jdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.model.Authority;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static guru.qa.niffler.data.jdbc.Connections.holder;

@ParametersAreNonnullByDefault
public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {

    private static final Config CFG = Config.getInstance();
    private final String url = CFG.authJdbcUrl();

    @SuppressWarnings("resource")
    @Override
    public void create(AuthorityEntity... authority) {
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "INSERT INTO \"authority\" (user_id, authority) VALUES (?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            for (AuthorityEntity a : authority) {
                ps.setObject(1, a.getUser().getId());
                ps.setString(2, a.getAuthority().name());

                ps.addBatch();
                ps.clearParameters();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("resource")
    @NotNull
    @Override
    public List<AuthorityEntity> findByUserId(UUID id) {
        List<AuthorityEntity> aeList = new ArrayList<>();
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "SELECT * FROM authority WHERE user_id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    aeList.add(convertResultSetToAuthorityEntity(rs));
                }
                return aeList;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("resource")
    @NotNull
    @Override
    public List<AuthorityEntity> findAll() {
        List<AuthorityEntity> aeList = new ArrayList<>();
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "SELECT * FROM authority"
        )) {
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    aeList.add(convertResultSetToAuthorityEntity(rs));
                }
                return aeList;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("resource")
    @Override
    public boolean delete(AuthorityEntity authority) {
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "DELETE FROM authority WHERE id = ?")) {
            ps.setObject(1, authority.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private AuthorityEntity convertResultSetToAuthorityEntity(ResultSet rs) {
        try {
            AuthorityEntity ae = new AuthorityEntity();

            ae.setId(rs.getObject("id", UUID.class));
//        ae.setUser(rs.getObject("user_id", UUID.class));
            ae.setAuthority(Authority.valueOf(rs.getString("authority")));

            return ae;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
