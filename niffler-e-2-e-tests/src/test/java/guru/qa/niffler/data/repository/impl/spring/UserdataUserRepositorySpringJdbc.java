package guru.qa.niffler.data.repository.impl.spring;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.spring.AbstractDaoSpring;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.mapper.row_mapper.UserdataUserEntityRowMapper;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.entity.userdata.FriendshipStatus.ACCEPTED;
import static guru.qa.niffler.data.entity.userdata.FriendshipStatus.PENDING;
import static guru.qa.niffler.data.tpl.DataSources.dataSource;

public class UserdataUserRepositorySpringJdbc extends AbstractDaoSpring<UserEntity> implements UserdataUserRepository {

    private static final Config CFG = Config.getInstance();

    public UserdataUserRepositorySpringJdbc() {
        super(dataSource(CFG.userdataJdbcUrl()), UserdataUserEntityRowMapper.instance);
    }

    @Override
    public UserEntity createUser(UserEntity user) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(
                con -> {
                    PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO \"user\" (username, currency, firstname, surname, full_name, photo, photo_small) " +
                                    "VALUES (?, ?, ?, ?, ?, ?, ? )",
                            Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, user.getUsername());
                    ps.setString(2, user.getCurrency().name());
                    ps.setString(3, user.getFirstname());
                    ps.setString(4, user.getSurname());
                    ps.setString(5, user.getFullname());
                    ps.setBytes(6, user.getPhoto());
                    ps.setBytes(7, user.getPhotoSmall());
                    return ps;
                }, kh
        );
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
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        "SELECT * FROM \"user\" WHERE username = ?",
                        rowMapper,
                        username
                )
        );
    }

    public List<UserEntity> findAll() {
        return jdbcTemplate.query(
                "SELECT * FROM \"user\"",
                rowMapper
        );
    }

    @Override
    public void addFriend(UserEntity requester, UserEntity addressee) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO friendship (requester_id, addressee_id, status) VALUES (?, ?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        if (i == 0) {
                            ps.setObject(1, requester.getId());
                            ps.setObject(2, addressee.getId());
                        } else {
                            ps.setObject(1, addressee.getId());
                            ps.setObject(2, requester.getId());
                        }
                        ps.setString(3, ACCEPTED.name());
                    }

                    @Override
                    public int getBatchSize() {
                        return 2;
                    }
                });
    }

    @Override
    public void addFriendshipRequest(UserEntity requester, UserEntity addressee) {
        jdbcTemplate.update(
                con -> {
                    PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO friendship (requester_id, addressee_id, status) " +
                                    "VALUES (?, ?, ? )");
                    ps.setObject(1, requester.getId());
                    ps.setObject(2, addressee.getId());
                    ps.setString(3, PENDING.name());
                    return ps;
                }
        );

    }
}
