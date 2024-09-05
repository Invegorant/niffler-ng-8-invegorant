package guru.qa.niffler.data.dao.impl.spring;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;

import static guru.qa.niffler.data.tpl.DataSources.dataSource;

/**
 * Класс предназначен для хранения и использования jdbcTemplate + rowMapper
 *
 * @param <Entity> объект, необходимый для маппинга после выполнения sql-запроса
 */
public abstract class AbstractDaoSpring<Entity> {

    protected final JdbcTemplate jdbcTemplate;
    protected final RowMapper<Entity> rowMapper;

    public AbstractDaoSpring(String jdbcUrl, RowMapper<Entity> rowMapper) {
        this.jdbcTemplate = new JdbcTemplate(dataSource(jdbcUrl));
        this.rowMapper = rowMapper;
    }

    public AbstractDaoSpring(DataSource dataSource, RowMapper<Entity> rowMapper) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.rowMapper = rowMapper;
    }
}
