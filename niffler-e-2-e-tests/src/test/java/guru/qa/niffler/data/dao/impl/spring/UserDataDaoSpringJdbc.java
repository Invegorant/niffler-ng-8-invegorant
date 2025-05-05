package guru.qa.niffler.data.dao.impl.spring;

import guru.qa.niffler.data.dao.UserDataDao;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.mapper.UdUserEntityRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;

//ToDo пофиксить для остальных методов
public class UserDataDaoSpringJdbc implements UserDataDao {

    private final JdbcTemplate jdbcTemplate;
    private static final UdUserEntityRowMapper rowMapper = UdUserEntityRowMapper.instance;


    public UserDataDaoSpringJdbc(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public UserEntity createUser(UserEntity user) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO \"user\" (username, currency, firstname, surname, photo, photo_small, full_name) " +
                            "VALUES (?,?,?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getCurrency().name());
            ps.setString(3, user.getFirstname());
            ps.setString(4, user.getSurname());
            ps.setBytes(5, user.getPhoto());
            ps.setBytes(6, user.getPhotoSmall());
            ps.setString(7, user.getFullname());
            return ps;
        }, kh);

        final UUID generatedKey = (UUID) kh.getKeys().get("id");
        user.setId(generatedKey);
        return user;
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        "SELECT * FROM \"user\" WHERE id = ?",
                        rowMapper,
                        id
                )
        );
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        UserEntity entity = jdbcTemplate.queryForObject(
                "SELECT * FROM \"user\" WHERE username = ?",
                rowMapper,
                username);
        return Optional.ofNullable(entity);
    }

    @Override
    public boolean deleteUser(UserEntity user) {
        int rowsAffected = jdbcTemplate.update("DELETE FROM \"user\" WHERE id = ?", user.getId());
        return rowsAffected > 0;
    }
}
