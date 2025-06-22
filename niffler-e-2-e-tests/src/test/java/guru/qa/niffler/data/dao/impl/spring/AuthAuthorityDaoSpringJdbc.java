package guru.qa.niffler.data.dao.impl.spring;

import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.mapper.row_mapper.AuthAuthorityEntityRowMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class AuthAuthorityDaoSpringJdbc extends AbstractDaoSpring<AuthorityEntity> implements AuthAuthorityDao {

    public AuthAuthorityDaoSpringJdbc(DataSource dataSource) {
        super(dataSource, AuthAuthorityEntityRowMapper.INSTANCE);
    }

    public AuthAuthorityDaoSpringJdbc(String jdbcUrl) {
        super(jdbcUrl, AuthAuthorityEntityRowMapper.INSTANCE);
    }

    @Override
    public void create(AuthorityEntity... authority) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO authority (user_id, authority) VALUES (? , ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setObject(1, authority[i].getUser().getId());
                        ps.setString(2, authority[i].getAuthority().name());
                    }

                    @Override
                    public int getBatchSize() {
                        return authority.length;
                    }
                }
        );
    }

    @NotNull
    @Override
    public List<AuthorityEntity> findByUserId(UUID id) {
        return jdbcTemplate.query(
                "SELECT * FROM \"authority\" WHERE user_id = ?",
                rowMapper,
                id);
    }

    @NotNull
    @Override
    public List<AuthorityEntity> findAll() {
        return jdbcTemplate.query(
                "SELECT * FROM \"authority\"",
                rowMapper
        );
    }

    @Override
    public boolean delete(AuthorityEntity authority) {
        int rowsAffected = jdbcTemplate.update("DELETE FROM \"authority\" WHERE id = ?", authority.getId());
        return rowsAffected > 0;
    }
}
