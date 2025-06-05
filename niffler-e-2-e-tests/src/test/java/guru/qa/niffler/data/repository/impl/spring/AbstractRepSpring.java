package guru.qa.niffler.data.repository.impl.spring;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import javax.sql.DataSource;

import static guru.qa.niffler.data.jdbc.DataSources.dataSource;

/**
 * Класс предназначен для хранения и использования jdbcTemplate + rowMapper
 *
 * @param <Entity> объект, необходимый для маппинга после выполнения sql-запроса
 */
public abstract class AbstractRepSpring<Entity> {

    protected final JdbcTemplate jdbcTemplate;
    protected final ResultSetExtractor<Entity> resultSetExtractor;

    public AbstractRepSpring(String jdbcUrl, ResultSetExtractor<Entity> resultSetExtractor) {
        this.jdbcTemplate = new JdbcTemplate(dataSource(jdbcUrl));
        this.resultSetExtractor = resultSetExtractor;
    }

    public AbstractRepSpring(DataSource dataSource, ResultSetExtractor<Entity> resultSetExtractor) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.resultSetExtractor = resultSetExtractor;
    }
}
