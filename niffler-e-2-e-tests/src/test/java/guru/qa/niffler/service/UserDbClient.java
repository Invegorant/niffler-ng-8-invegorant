package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.UserDataDao;
import guru.qa.niffler.data.dao.impl.jdbc.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.jdbc.AuthUserDaoJdbc;
import guru.qa.niffler.data.dao.impl.jdbc.UserDataDaoJdbc;
import guru.qa.niffler.data.dao.impl.spring.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.spring.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.spring.UserDataDaoSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.impl.AuthUserRepositoryJdbc;
import guru.qa.niffler.data.tpl.DataSources;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.Authority;
import guru.qa.niffler.model.UserJson;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;

import static guru.qa.niffler.data.tpl.DataSources.dataSource;
import static guru.qa.niffler.test.web.AbstractTest.DEFAULT_PASSWORD;

public class UserDbClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    /**** JDBC DAO ****/
    private final AuthUserDao authUserDaoJdbc = new AuthUserDaoJdbc();
    private final UserDataDao userDataDaoJdbc = new UserDataDaoJdbc();
    private final AuthAuthorityDao authAuthorityDaoJdbc = new AuthAuthorityDaoJdbc();

    /**** SPRING DAO ****/
    private final AuthUserDao authUserDaoSpring = new AuthUserDaoSpringJdbc(dataSource(CFG.authJdbcUrl()));
    private final UserDataDao userDataDaoSpring = new UserDataDaoSpringJdbc(dataSource(CFG.userdataJdbcUrl()));
    private final AuthAuthorityDao authAuthorityDaoSpring = new AuthAuthorityDaoSpringJdbc(dataSource(CFG.authJdbcUrl()));

    /**** REPOSITORIES ****/
    private final AuthUserRepository authUserRepository = new AuthUserRepositoryJdbc();

    private final TransactionTemplate txTemplate = new TransactionTemplate(
            new JdbcTransactionManager(DataSources.dataSource(CFG.authJdbcUrl()))
    );

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );

    private final TransactionTemplate chainedTxTemplate = new TransactionTemplate(
            new ChainedTransactionManager(
                    new JdbcTransactionManager(dataSource(CFG.authJdbcUrl())),
                    new JdbcTransactionManager(dataSource(CFG.userdataJdbcUrl()))
            )
    );

    /**
     * Создает УЗ используя Spring JDBC + xaTransactionTemplate
     */
    public UserJson createUserSpringTx(UserJson user) {
        return xaTransactionTemplate.execute(() -> createUserSpringJdbc(user));
    }

    /**
     * Создает УЗ используя Spring JDBC без xaTransactionTemplate
     */
    public UserJson createUserSpringNoTx(UserJson user) {
        return createUserSpringJdbc(user);
    }

    /**
     * Создает УЗ используя JDBC + xaTransactionTemplate
     */
    public UserJson createUserJdbcTx(UserJson user) {
        return xaTransactionTemplate.execute(() -> createUserJdbc(user));
    }

    /**
     * Создает УЗ используя JDBC без xaTransactionTemplate
     */
    public UserJson createUserJdbcNoTx(UserJson user) {
        return createUserJdbc(user);
    }


    /**
     * Создает УЗ используя Spring JDBC + ChainedTransactionManager
     */
    public UserJson createUserSpringJdbcChainedTx(UserJson user) {
        return chainedTxTemplate.execute(status -> createUserSpringJdbc(user));
    }

    private UserJson createUserJdbc(UserJson user) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(user.username());
        authUser.setPassword(pe.encode(DEFAULT_PASSWORD));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);

        authUser.setAuthorities(
                Arrays.stream(Authority.values()).map(
                        e -> {
                            AuthorityEntity ae = new AuthorityEntity();
                            ae.setUser(authUser);
                            ae.setAuthority(e);
                            return ae;
                        }
                ).toList()
        );

        authUserDaoJdbc.create(authUser);
        return UserJson.fromEntity(
                userDataDaoJdbc.createUser(UserEntity.fromJson(user)),
                null
        );
    }

    private UserJson createUserSpringJdbc(UserJson user) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(user.username());
        authUser.setPassword(pe.encode(DEFAULT_PASSWORD));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);

        authUser.setAuthorities(
                Arrays.stream(Authority.values()).map(
                        e -> {
                            AuthorityEntity ae = new AuthorityEntity();
                            ae.setUser(authUser);
                            ae.setAuthority(e);
                            return ae;
                        }
                ).toList()
        );

        authUserDaoSpring.create(authUser);
        return UserJson.fromEntity(
                userDataDaoSpring.createUser(UserEntity.fromJson(user)),
                null
        );
    }

    private UserJson createUserRepository(UserJson user) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(user.username());
        authUser.setPassword(pe.encode(DEFAULT_PASSWORD));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);
        authUser.setAuthorities(
                Arrays.stream(Authority.values()).map(
                        e -> {
                            AuthorityEntity ae = new AuthorityEntity();
                            ae.setUser(authUser);
                            ae.setAuthority(e);
                            return ae;
                        }
                ).toList()
        );
        authUserRepository.create(authUser);

        return UserJson.fromEntity(
                userDataDaoSpring.createUser(UserEntity.fromJson(user)),
                null
        );
    }
}
