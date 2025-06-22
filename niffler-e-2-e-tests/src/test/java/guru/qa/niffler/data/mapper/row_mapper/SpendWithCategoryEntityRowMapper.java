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
public class SpendWithCategoryEntityRowMapper implements RowMapper<SpendEntity> {

    public static final SpendWithCategoryEntityRowMapper INSTANCE = new SpendWithCategoryEntityRowMapper();

    private SpendWithCategoryEntityRowMapper() {
    }

    @Nullable
    @Override
    public SpendEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        SpendEntity se = new SpendEntity();
        CategoryEntity ce = new CategoryEntity();
        ce.setId(rs.getObject("category_id", UUID.class));
        ce.setName(rs.getString("name"));
        ce.setUsername(rs.getString("username"));
        ce.setArchived(rs.getBoolean("archived"));

        se.setId(rs.getObject("id", UUID.class));
        se.setUsername(rs.getString("username"));
        se.setSpendDate(rs.getDate("spend_date"));
        se.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
        se.setAmount(rs.getDouble("amount"));
        se.setDescription(rs.getString("description"));
        se.setCategory(ce);
        return se;
    }
}
