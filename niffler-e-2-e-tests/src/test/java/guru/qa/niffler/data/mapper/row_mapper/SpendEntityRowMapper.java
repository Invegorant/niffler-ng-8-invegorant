package guru.qa.niffler.data.mapper.row_mapper;

import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CurrencyValues;
import org.springframework.jdbc.core.RowMapper;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class SpendEntityRowMapper implements RowMapper<SpendEntity> {

    public static final SpendEntityRowMapper INSTANCE = new SpendEntityRowMapper();

    private SpendEntityRowMapper() {
    }

    @Nullable
    @Override
    public SpendEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        SpendEntity result = new SpendEntity();
        result.setId(rs.getObject("id", UUID.class));
        result.setUsername(rs.getString("username"));
        result.setSpendDate(rs.getDate("spend_date"));
        result.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
        result.setAmount(rs.getDouble("amount"));
        result.setDescription(rs.getString("description"));

        CategoryEntity category = new CategoryEntity(rs.getObject("category_id", UUID.class));
        category.setUsername(rs.getString("username"));
        category.setName(rs.getString("category_name"));
        category.setArchived(rs.getBoolean("category_archived"));

        result.setCategory(category);
        return result;
    }
}
