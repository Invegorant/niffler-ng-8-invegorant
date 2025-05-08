package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UserDataDao;
import guru.qa.niffler.data.dao.impl.jdbc.UserDataDaoJdbc;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.model.UserJson;

import java.util.Optional;
import java.util.UUID;

public class UserDataUserDbClient {

    private static final Config CFG = Config.getInstance();

    private final UserDataDao userDataDao = new UserDataDaoJdbc();

    private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(
            CFG.spendJdbcUrl()
    );

    public UserJson createUser(UserJson user) {
        return jdbcTxTemplate.execute(() -> UserJson.fromEntity(
                        userDataDao.createUser(
                                UserEntity.fromJson(user)
                        )
                )
        );
    }

    public Optional<UserJson> findUserByUsername(String username) {
        return jdbcTxTemplate.execute(() -> userDataDao
                .findByUsername(username)
                .map(UserJson::fromEntity)
        );
    }

    public Optional<UserJson> findUserById(UUID id) {
        return jdbcTxTemplate.execute(() -> userDataDao
                .findById(id)
                .map(UserJson::fromEntity)
        );
    }

    public void deleteUser(UserJson user) {
        jdbcTxTemplate.execute(() -> userDataDao.deleteUser(UserEntity.fromJson(user)));
    }
}
