package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.jdbc.UserDataDaoJdbc;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.UserJson;

import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.Databases.transaction;
import static java.sql.Connection.TRANSACTION_READ_UNCOMMITTED;

public class UserdataUserDbClient {

    private static final Config CFG = Config.getInstance();

    public UserJson createUser(UserJson user) {
        return transaction(connection -> {
                    return UserJson.fromEntity(new UserDataDaoJdbc(connection).createUser(UserEntity.fromJson(user)));
                }, CFG.userdataJdbcUrl(),
                TRANSACTION_READ_UNCOMMITTED
        );
    }

    public Optional<UserJson> findUserByUsername(String username) {
        return transaction(connection -> {
                    return new UserDataDaoJdbc(connection)
                            .findByUsername(username)
                            .map(UserJson::fromEntity);
                },
                CFG.userdataJdbcUrl(),
                TRANSACTION_READ_UNCOMMITTED
        );
    }

    public Optional<UserJson> findUserById(UUID id) {
        return transaction(connection -> {
                    return new UserDataDaoJdbc(connection)
                            .findById(id)
                            .map(UserJson::fromEntity);
                },
                CFG.userdataJdbcUrl(),
                TRANSACTION_READ_UNCOMMITTED
        );
    }

    public void deleteUser(UserJson user) {
        transaction(connection -> {
                    new UserDataDaoJdbc(connection).deleteUser(UserEntity.fromJson(user));
                },
                CFG.userdataJdbcUrl(),
                TRANSACTION_READ_UNCOMMITTED
        );
    }
}
