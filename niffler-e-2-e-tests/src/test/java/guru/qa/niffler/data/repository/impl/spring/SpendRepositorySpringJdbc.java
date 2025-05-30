package guru.qa.niffler.data.repository.impl.spring;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.spring.CategoryDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.spring.SpendDaoSpringJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.mapper.row_mapper.SpendWithCategoryEntityRowMapper;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.jdbc.DataSources;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Optional;
import java.util.UUID;

public class SpendRepositorySpringJdbc implements SpendRepository {

    private static final Config CFG = Config.getInstance();

    private final CategoryDao categoryDao = new CategoryDaoSpringJdbc(CFG.spendJdbcUrl());
    private final SpendDao spendDao = new SpendDaoSpringJdbc(CFG.spendJdbcUrl());

    @Override
    public SpendEntity createSpend(SpendEntity spend) {
        CategoryEntity category = spend.getCategory();

        if (category.getId() == null) {
            String categoryName = category.getName();
            String username = category.getUsername();

            if (categoryName != null && username != null) {
                category = categoryDao.findCategoryByUsernameAndCategoryName(categoryName, username)
                        .orElseGet(() -> categoryDao.createCategory(spend.getCategory()));
            }
        }
        spend.setCategory(category);
        return spendDao.createSpend(spend);
    }

    @Override
    public SpendEntity updateSpend(SpendEntity spend) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        jdbcTemplate.update(con -> {
                    PreparedStatement ps = con.prepareStatement(
                            """
                                    UPDATE spend \
                                    SET username = ?,\
                                    spend_date = ?,\
                                    currency = ?,\
                                    amount = ?,\
                                    description = ?,\
                                    category_id = ?\
                                    WHERE id = ?""");

                    ps.setString(1, spend.getUsername());
                    ps.setDate(2, new Date(spend.getSpendDate().getTime()));
                    ps.setString(3, spend.getCurrency().name());
                    ps.setDouble(4, spend.getAmount());
                    ps.setString(5, spend.getDescription());
                    ps.setObject(6, spend.getCategory().getId());
                    ps.setObject(7, spend.getId());
                    return ps;
                }
        );
        return spend;
    }

    @Override
    public CategoryEntity createCategory(CategoryEntity category) {
        return categoryDao.createCategory(category);
    }

    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        return categoryDao.findCategoryById(id);
    }

    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String name) {
        return categoryDao.findCategoryByUsernameAndCategoryName(username, name);
    }

    @Override
    public Optional<SpendEntity> findSpendById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));

        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            """
                                    SELECT s.*,\
                                    c.id AS category_id, \
                                    c.name,\
                                    c.username,\
                                    c.archived\
                                     FROM spend s \
                                    JOIN category c ON s.category_id = c.id \
                                    WHERE s.id = ?""",
                            SpendWithCategoryEntityRowMapper.INSTANCE,
                            id
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<SpendEntity> findByUsernameAndSpendDescription(String username, String description) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));

        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            """
                                    SELECT s.*,\
                                    c.id AS category_id, \
                                    c.name,\
                                    c.username,\
                                    c.archived\
                                     FROM spend s \
                                    JOIN category c ON s.category_id = c.id \
                                    WHERE s.username = ? AND s.description = ?""",
                            SpendWithCategoryEntityRowMapper.INSTANCE,
                            username, description
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void removeSpend(SpendEntity spend) {
        spendDao.deleteSpend(spend);
    }

    @Override
    public void removeCategory(CategoryEntity category) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        jdbcTemplate.update(con -> {
                    PreparedStatement ps = con.prepareStatement(
                            """
                                    WITH deleted_spend AS (\
                                    DELETE FROM spend WHERE category_id = ?)\
                                    DELETE FROM category WHERE id = ?"""
                    );
                    ps.setObject(1, category.getId());
                    ps.setObject(2, category.getId());
                    return ps;
                }
        );
    }
}
