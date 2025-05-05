package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.impl.jdbc.AuthUserDaoJdbc;
import guru.qa.niffler.data.dao.impl.jdbc.UserDataDaoJdbc;
import guru.qa.niffler.data.dao.impl.spring.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.spring.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.spring.UserDataDaoSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.AuthUserJson;
import guru.qa.niffler.model.Authority;
import guru.qa.niffler.model.UserJson;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.UUID;

import static guru.qa.niffler.data.Databases.dataSource;
import static guru.qa.niffler.data.Databases.xaTransaction;
import static guru.qa.niffler.test.web.AbstractTest.DEFAULT_PASSWORD;
import static java.sql.Connection.TRANSACTION_READ_UNCOMMITTED;

public class UserDbClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

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
                    return UserJson.fromEntity(new UserDataDaoJdbc(connection).createUser(userEntity)).id();
                },
                CFG.userdataJdbcUrl()
        );

        return xaTransaction(TRANSACTION_READ_UNCOMMITTED, xaFunAuthUser, xaFunUser);
    }

    public UserJson createUserSpringJdbc(UserJson user) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(user.username());
        authUser.setPassword(pe.encode(DEFAULT_PASSWORD));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);

        AuthUserEntity createdAuthUser = new AuthUserDaoSpringJdbc(dataSource(CFG.authJdbcUrl()))
                .create(authUser);

        AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
                e -> {
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setUserId(createdAuthUser.getId());
                    ae.setAuthority(e);
                    return ae;
                }
        ).toArray(AuthorityEntity[]::new);

        new AuthAuthorityDaoSpringJdbc(dataSource(CFG.authJdbcUrl()))
                .create(authorityEntities);

        return UserJson.fromEntity(
                new UserDataDaoSpringJdbc(dataSource(CFG.userdataJdbcUrl()))
                        .createUser(
                                UserEntity.fromJson(user)
                        )
        );
    }
}
