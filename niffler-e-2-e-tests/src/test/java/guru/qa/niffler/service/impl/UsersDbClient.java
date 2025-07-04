package guru.qa.niffler.service.impl;

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
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.repository.impl.hibernate.AuthUserRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.hibernate.UserdataUserRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.jdbc.AuthUserRepositoryJdbc;
import guru.qa.niffler.data.repository.impl.jdbc.UserdataUserRepositoryJdbc;
import guru.qa.niffler.data.repository.impl.spring.AuthUserRepositorySpringJdbc;
import guru.qa.niffler.data.repository.impl.spring.UserdataUserRepositorySpringJdbc;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.*;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.utils.RandomDataUtils;
import io.qameta.allure.Step;
import jakarta.persistence.EntityNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jdbc.DataSources.dataSource;
import static guru.qa.niffler.test.web.AbstractTest.DEFAULT_PASSWORD;
import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public class UsersDbClient implements UsersClient {

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

    /**** REPOSITORIES - JDBC ****/
    private final AuthUserRepository authUserRepositoryJdbc = new AuthUserRepositoryJdbc();
    private final UserdataUserRepository userdataUserRepositoryJdbc = new UserdataUserRepositoryJdbc();

    /**** REPOSITORIES - SPRING JDBC ****/
    private final AuthUserRepository authUserRepositorySpring = new AuthUserRepositorySpringJdbc();
    private final UserdataUserRepository userdataUserRepositorySpring = new UserdataUserRepositorySpringJdbc();

    /**** REPOSITORIES - HIBERNATE ****/
    private final AuthUserRepository authUserRepositoryHibernate = new AuthUserRepositoryHibernate();
    private final UserdataUserRepository userdataUserRepositoryHibernate = new UserdataUserRepositoryHibernate();

    private final TransactionTemplate txTemplate = new TransactionTemplate(
            new JdbcTransactionManager(dataSource(CFG.authJdbcUrl()))
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
    @Step("Create User with Spring Tx")
    @Nonnull
    public UserJson createUserSpringTx(UserJson user) {
        return requireNonNull(xaTransactionTemplate.execute(() -> createUserSpringJdbc(user)));
    }

    /**
     * Создает УЗ используя Spring JDBC без xaTransactionTemplate
     */
    @Step("Create User without Spring Tx")
    @Nonnull
    public UserJson createUserSpringNoTx(UserJson user) {
        return createUserSpringJdbc(user);
    }

    /**
     * Создает УЗ используя JDBC + xaTransactionTemplate
     */
    @Step("Create User with JDBC Tx")
    @Nonnull
    public UserJson createUserJdbcTx(UserJson user) {
        return requireNonNull(xaTransactionTemplate.execute(() -> createUserJdbc(user)));
    }

    /**
     * Создает УЗ используя JDBC без xaTransactionTemplate
     */
    @Step("Create User without JDBC Tx")
    @Nonnull
    public UserJson createUserJdbcNoTx(UserJson user) {
        return createUserJdbc(user);
    }


    /**
     * Создает УЗ используя Spring JDBC + ChainedTransactionManager
     */
    @Step("Create User with JDBC Chained Tx")
    @Nonnull
    public UserJson createUserSpringJdbcChainedTx(UserJson user) {
        return requireNonNull(chainedTxTemplate.execute(status -> createUserSpringJdbc(user)));
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

    @Step("Create User")
    @NotNull
    @Override
    public UserJson createUser(String username, String password) {
        return requireNonNull(xaTransactionTemplate.execute(() -> {
                    AuthUserEntity authUser = createAuthUserEntityRepository(username, password);
                    authUserRepositoryHibernate.create(authUser);
                    return UserJson.fromEntity(
                            userdataUserRepositoryHibernate.createUser(userEntity(username)),
                            null
                    ).withTestData(new TestData(password));
                }
        ));
    }

    @Step("Add income invitations to user: {targetUser}")
    @Override
    public void createIncomeInvitations(UserJson targetUser, int count) {
        if (count > 0) {
            UserEntity targetEntity = findUserEntity(targetUser);

            for (int i = 0; i < count; i++) {
                targetUser.testData()
                        .incomeInvitations()
                        .add(UserJson.fromEntity(
                                requireNonNull(xaTransactionTemplate.execute(() -> {
                                                    String username = RandomDataUtils.randomUsername();
                                                    AuthUserEntity authUser = createAuthUserEntityRepository(username, DEFAULT_PASSWORD);
                                                    authUserRepositoryHibernate.create(authUser);
                                                    UserEntity addressee = userdataUserRepositoryHibernate.createUser(userEntity(username));
                                                    userdataUserRepositoryHibernate.sendInvitation(addressee, targetEntity);
                                                    return addressee;
                                                }
                                )),
                                        FriendshipStatus.INVITE_RECEIVED
                                )
                        );
            }
        }
    }

    @Step("Add outcome invitations to user: {targetUser}")
    @Override
    public void createOutcomeInvitations(UserJson targetUser, int count) {
        if (count > 0) {
            UserEntity targetEntity = findUserEntity(targetUser);

            for (int i = 0; i < count; i++) {
                targetUser.testData()
                        .outcomeInvitations()
                        .add(UserJson.fromEntity(
                                requireNonNull(xaTransactionTemplate.execute(() -> {
                                                    String username = RandomDataUtils.randomUsername();
                                                    AuthUserEntity authUser = createAuthUserEntityRepository(username, DEFAULT_PASSWORD);
                                                    authUserRepositoryHibernate.create(authUser);
                                                    UserEntity addressee = userdataUserRepositoryHibernate.createUser(userEntity(username));
                                                    userdataUserRepositoryHibernate.sendInvitation(targetEntity, addressee);
                                                    return addressee;
                                                }
                                )),
                                        FriendshipStatus.INVITE_SENT
                                )
                        );
            }
        }
    }

    @Step("Add friends to user: {targetUser}")
    @Override
    public void createFriends(UserJson targetUser, int count) {
        if (count > 0) {
            UserEntity targetEntity = findUserEntity(targetUser);

            for (int i = 0; i < count; i++) {
                targetUser.testData()
                        .friends()
                        .add(UserJson.fromEntity(
                                requireNonNull(xaTransactionTemplate.execute(() -> {
                                                    String username = RandomDataUtils.randomUsername();
                                                    AuthUserEntity authUser = createAuthUserEntityRepository(username, DEFAULT_PASSWORD);
                                                    authUserRepositoryHibernate.create(authUser);
                                                    UserEntity addressee = userdataUserRepositoryHibernate.createUser(userEntity(username));
                                                    userdataUserRepositoryHibernate.addFriend(targetEntity, addressee);
                                                    return addressee;
                                                }
                                )),
                                        FriendshipStatus.FRIEND
                                )
                        );
            }
        }
    }

    @Step("Delete user: {user}")
    @Override
    public void remove(UserJson user) {
        xaTransactionTemplate.execute(() -> {
                    Optional<AuthUserEntity> authUserOpt = authUserRepositoryHibernate.findByUsername(user.username());
                    authUserOpt.ifPresent(authUserRepositoryHibernate::remove);
                    userdataUserRepositoryHibernate.removeFriend(UserEntity.fromJson(user));
                    return null;
                }
        );
    }

    private UserEntity userEntity(String username) {
        UserEntity ue = new UserEntity();
        ue.setUsername(username);
        ue.setCurrency(CurrencyValues.RUB);
        return ue;
    }

    private AuthUserEntity createAuthUserEntityRepository(String username, String password) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(username);
        authUser.setPassword(pe.encode(password));
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
        return authUser;
    }

    private UserEntity findUserEntity(UserJson targetUser) {
        UUID id = targetUser.id();
        String username = targetUser.username();

        if (id == null && username == null) {
            throw new IllegalArgumentException("User ID and username cannot both be null");
        }

        Optional<UserEntity> entityOpt = (id != null)
                ? userdataUserRepositoryHibernate.findById(id)
                : Optional.empty();

        return entityOpt.or(() ->
                username != null
                        ? userdataUserRepositoryHibernate.findByUsername(username)
                        : Optional.empty()
        ).orElseThrow(() -> new EntityNotFoundException("Target user not found by id or username"));
    }
}
