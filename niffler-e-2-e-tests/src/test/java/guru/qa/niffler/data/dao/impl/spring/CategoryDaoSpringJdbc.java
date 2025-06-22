package guru.qa.niffler.data.dao.impl.spring;

import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.mapper.row_mapper.CategoryEntityRowMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class CategoryDaoSpringJdbc extends AbstractDaoSpring<CategoryEntity> implements CategoryDao {

    public CategoryDaoSpringJdbc(String jdbcUrl) {
        super(jdbcUrl, CategoryEntityRowMapper.INSTANCE);
    }

    @NotNull
    @Override
    public CategoryEntity createCategory(CategoryEntity category) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO category (username, name, archived) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, category.getUsername());
            ps.setString(2, category.getName());
            ps.setBoolean(3, category.isArchived());

            return ps;
        }, kh);

        final UUID generatedKey = (UUID) kh.getKeys().get("id");
        category.setId(generatedKey);
        return category;
    }

    @NotNull
    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        return Optional.ofNullable(jdbcTemplate.queryForObject(
                        "SELECT * FROM category WHERE id = ?",
                        rowMapper,
                        id
                )
        );
    }

    @NotNull
    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        return Optional.ofNullable(jdbcTemplate.queryForObject(
                        "SELECT * FROM category WHERE username = ? AND name = ?",
                        rowMapper,
                        username,
                        categoryName
                )
        );
    }

    @NotNull
    @Override
    public List<CategoryEntity> findAllByUsername(String username) {
        return jdbcTemplate.query(
                "SELECT * FROM category WHERE username = ?",
                rowMapper,
                username
        );
    }

    @NotNull
    @Override
    public List<CategoryEntity> findAll() {
        return jdbcTemplate.query(
                "SELECT * FROM category",
                rowMapper
        );
    }

    @Override
    public boolean deleteCategory(CategoryEntity category) {
        int rowsAffected = jdbcTemplate.update("DELETE FROM category WHERE id = ?");
        return rowsAffected > 0;
    }
}
