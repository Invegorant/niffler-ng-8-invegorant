package guru.qa.niffler.data.repository.impl.spring;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UserDataDao;
import guru.qa.niffler.data.dao.impl.spring.AbstractDaoSpring;
import guru.qa.niffler.data.dao.impl.spring.UserDataDaoSpringJdbc;
import guru.qa.niffler.data.entity.userdata.FriendshipEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.mapper.row_mapper.UserdataUserEntityRowMapper;
import guru.qa.niffler.data.repository.UserdataUserRepository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.entity.userdata.FriendshipStatus.ACCEPTED;
import static guru.qa.niffler.data.entity.userdata.FriendshipStatus.PENDING;
import static guru.qa.niffler.data.tpl.DataSources.dataSource;

public class UserdataUserRepositorySpringJdbc extends AbstractDaoSpring<UserEntity> implements UserdataUserRepository {

    private static final Config CFG = Config.getInstance();

    private final UserDataDao userDataDao = new UserDataDaoSpringJdbc(dataSource(CFG.userdataJdbcUrl()));

    public UserdataUserRepositorySpringJdbc() {
        super(dataSource(CFG.userdataJdbcUrl()), UserdataUserEntityRowMapper.INSTANCE);
    }

    @Override
    public UserEntity createUser(UserEntity user) {
        return userDataDao.createUser(user);
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        Optional<UserEntity> userOpt = userDataDao.findById(id);
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }

        UserEntity userEntity = userOpt.get();

        List<UserEntity> listOfAcceptedFriends = jdbcTemplate.query(
                "SELECT * FROM friendship f " +
                        "JOIN \"user\" u ON f.addressee_id = u.id " +
                        "WHERE requester_id = ? AND status = ? ",
                rowMapper,
                id,
                ACCEPTED.name()
        );

        List<UserEntity> listOfPendingFriends = jdbcTemplate.query(
                "SELECT * FROM friendship f " +
                        "JOIN \"user\" u ON f.addressee_id = u.id " +
                        "WHERE requester_id = ? AND status = ? ",
                rowMapper,
                id,
                PENDING.name()
        );

        List<UserEntity> listInvitations = jdbcTemplate.query(
                "SELECT * FROM friendship f " +
                        "JOIN \"user\" u ON f.requester_id = u.id " +
                        "WHERE addressee_id = ? AND status = ? ",
                rowMapper,
                id,
                PENDING.name()
        );

        userEntity.addInvitations(listInvitations.toArray(UserEntity[]::new));
        userEntity.addFriends(ACCEPTED, listOfAcceptedFriends.toArray(UserEntity[]::new));
        userEntity.addFriends(PENDING, listOfPendingFriends.toArray(UserEntity[]::new));

        return Optional.of(userEntity);
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        Optional<UserEntity> userOpt = userDataDao.findByUsername(username);
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }

        UserEntity userEntity = userOpt.get();

        List<UserEntity> listOfAcceptedFriends = jdbcTemplate.query(
                "SELECT * FROM friendship f " +
                        "JOIN \"user\" u ON f.addressee_id = u.id " +
                        "WHERE requester_id = ? AND status = ? ",
                rowMapper,
                userEntity.getId(),
                ACCEPTED.name()
        );

        List<UserEntity> listOfPendingFriends = jdbcTemplate.query(
                "SELECT * FROM friendship f " +
                        "JOIN \"user\" u ON f.addressee_id = u.id " +
                        "WHERE requester_id = ? AND status = ? ",
                rowMapper,
                userEntity.getId(),
                PENDING.name()
        );

        List<UserEntity> listInvitations = jdbcTemplate.query(
                "SELECT * FROM friendship f " +
                        "JOIN \"user\" u ON f.requester_id = u.id " +
                        "WHERE addressee_id = ? AND status = ? ",
                rowMapper,
                userEntity.getId(),
                PENDING.name()
        );

        userEntity.addInvitations(listInvitations.toArray(UserEntity[]::new));
        userEntity.addFriends(ACCEPTED, listOfAcceptedFriends.toArray(UserEntity[]::new));
        userEntity.addFriends(PENDING, listOfPendingFriends.toArray(UserEntity[]::new));

        return Optional.of(userEntity);
    }

    @Override
    public UserEntity update(UserEntity user) {
        jdbcTemplate.update(con -> {
            PreparedStatement userPs = con.prepareStatement(
                    "UPDATE \"user\" SET " +
                            "username = ?," +
                            "currency = ?," +
                            "firstname = ?," +
                            "surname = ?," +
                            "photo = ?," +
                            "photo_small = ?," +
                            "full_name = ? " +
                            "WHERE id = ?"
            );
            userPs.setString(1, user.getUsername());
            userPs.setString(2, user.getCurrency().name());
            userPs.setString(3, user.getFirstname());
            userPs.setString(4, user.getSurname());
            userPs.setBytes(5, user.getPhoto());
            userPs.setBytes(6, user.getPhotoSmall());
            userPs.setString(7, user.getFullname());
            userPs.setObject(8, user.getId());
            return userPs;
        });

        for (FriendshipEntity fe : user.getFriendshipRequests()) {
            addFriend(fe.getRequester(), fe.getAddressee());
        }

        for (FriendshipEntity fe : user.getFriendshipAddressees()) {
            sendInvitation(fe.getRequester(), fe.getAddressee());
        }
        return user;
    }

    @Override
    public void sendInvitation(UserEntity requester, UserEntity addressee) {
        jdbcTemplate.update(
                """
                        INSERT INTO friendship (requester_id, addressee_id, status) \
                        VALUES (?, ?, ?) \
                        ON CONFLICT (requester_id, addressee_id) \
                        DO UPDATE SET status = ?, created_date = NOW()""",
                requester.getId(), addressee.getId(), PENDING.name(), PENDING.name());
        requester.addFriends(PENDING, addressee);
        addressee.addInvitations(requester);
    }

    @Override
    public void addFriend(UserEntity requester, UserEntity addressee) {
        List<Object[]> batchArgs = List.of(
                new Object[]{requester.getId(), addressee.getId(), ACCEPTED.name(), ACCEPTED.name()},
                new Object[]{addressee.getId(), requester.getId(), ACCEPTED.name(), ACCEPTED.name()}
        );
        jdbcTemplate.batchUpdate(
                """
                        INSERT INTO friendship (requester_id, addressee_id, status) \
                        VALUES (?, ?, ?)\
                        ON CONFLICT (requester_id, addressee_id) \
                        DO UPDATE SET status = ?, created_date = NOW()""",
                batchArgs
        );
        requester.addFriends(ACCEPTED, addressee);
        addressee.addFriends(ACCEPTED, requester);
    }

    @Override
    public void removeFriend(UserEntity user) {
        jdbcTemplate.update(
                """
                        WITH deleteted_friendship AS \
                        (DELETE FROM friendship WHERE requester_id = ? OR addressee_id = ?)\
                        DELETE FROM "user" WHERE id = ?""",
                user.getId(), user.getId(), user.getId()
        );
    }
}
