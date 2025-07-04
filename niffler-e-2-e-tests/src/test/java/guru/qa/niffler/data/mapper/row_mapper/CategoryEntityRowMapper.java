package guru.qa.niffler.data.mapper.row_mapper;

import guru.qa.niffler.data.entity.spend.CategoryEntity;
import org.springframework.jdbc.core.RowMapper;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class CategoryEntityRowMapper implements RowMapper<CategoryEntity> {

    public static final CategoryEntityRowMapper INSTANCE = new CategoryEntityRowMapper();

    private CategoryEntityRowMapper() {
    }

    @Nullable
    @Override
    public CategoryEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        CategoryEntity ce = new CategoryEntity();

        ce.setId(rs.getObject("id", UUID.class));
        ce.setUsername(rs.getString("username"));
        ce.setName(rs.getString("name"));
        ce.setArchived(rs.getBoolean("archived"));

        return ce;
    }
}
