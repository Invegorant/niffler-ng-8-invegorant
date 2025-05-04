package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.dao.impl.UserdataDaoJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.AuthUserJson;
import guru.qa.niffler.model.UserJson;

import java.util.UUID;

import static guru.qa.niffler.data.Databases.xaTransaction;
import static java.sql.Connection.TRANSACTION_READ_UNCOMMITTED;

public class UserDbClient {

    private static final Config CFG = Config.getInstance();

    public UUID createUser(AuthUserJson authUser, UserJson userdataUser) {

        Databases.XaFunction<UUID> xaFunAuthUser = new Databases.XaFunction<>(
                connection -> {
                    AuthUserEntity authUserEntity = AuthUserEntity.fromJson(authUser);
                    return AuthUserJson.fromEntity(new AuthUserDaoJdbc(connection).create(authUserEntity)).id();
                },
                CFG.authJdbcUrl()
        );

        Databases.XaFunction<UUID> xaFunUser = new Databases.XaFunction<>(
                connection -> {
                    UserEntity userEntity = UserEntity.fromJson(userdataUser);
                    return UserJson.fromEntity(new UserdataDaoJdbc(connection).createUser(userEntity)).id();
                },
                CFG.userdataJdbcUrl()
        );

        return xaTransaction(TRANSACTION_READ_UNCOMMITTED, xaFunAuthUser, xaFunUser);
    }
}
