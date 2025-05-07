package guru.qa.niffler.data.dao.impl.spring;

import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.mapper.SpendEntityRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpendDaoSpringJdbc extends AbstractDaoSpring<SpendEntity> implements SpendDao {

    public SpendDaoSpringJdbc(String jdbcUrl) {
        super(jdbcUrl, SpendEntityRowMapper.INSTANCE);
    }

    @Override
    public SpendEntity createSpend(SpendEntity spend) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    """
                            INSERT INTO spend (username, spend_date, currency, amount, description, category_id)
                            VALUES ( ?, ?, ?, ?, ?, ?)""",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, spend.getUsername());
            ps.setDate(2, spend.getSpendDate());
            ps.setString(3, spend.getCurrency().name());
            ps.setDouble(4, spend.getAmount());
            ps.setString(5, spend.getDescription());
            ps.setObject(6, spend.getCategory().getId());

            return ps;
        }, kh);

        final UUID generatedKey = (UUID) kh.getKeys().get("id");
        spend.setId(generatedKey);
        return spend;
    }

    @Override
    public Optional<SpendEntity> findSpendById(UUID id) {
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        "SELECT * FROM spend WHERE id = ?",
                        rowMapper,
                        id
                )
        );
    }

    @Override
    public List<SpendEntity> findAllByUsername(String username) {
        return jdbcTemplate.query(
                "SELECT * FROM spend WHERE username = ?",
                rowMapper,
                username
        );
    }

    @Override
    public List<SpendEntity> findAll() {
        return jdbcTemplate.query(
                "SELECT * FROM spend",
                rowMapper
        );
    }

    @Override
    public boolean deleteSpend(SpendEntity spend) {
        int rowsAffected = jdbcTemplate.update("DELETE FROM spend WHERE id = ?");
        return rowsAffected > 0;
    }
}
