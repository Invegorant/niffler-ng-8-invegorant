package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.impl.jdbc.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.jdbc.AuthUserDaoJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.model.AuthUserJson;
import guru.qa.niffler.model.Authority;

public class AuthDbClient {

    private static final Config CFG = Config.getInstance();

    private final AuthUserDao authUserDao = new AuthUserDaoJdbc();
    private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoJdbc();

    private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(
            CFG.authJdbcUrl()
    );

    public AuthUserJson createUser(AuthUserJson user) {
        return jdbcTxTemplate.execute(() -> {
                    AuthUserEntity authUser = authUserDao.create(AuthUserEntity.fromJson(user));
                    AuthorityEntity authority = new AuthorityEntity();

                    authority.setUserId(authUser.getId());
                    authority.setAuthority(Authority.read);
                    authAuthorityDao.create(authority);
                    authority.setAuthority(Authority.write);
                    authAuthorityDao.create(authority);

                    return AuthUserJson.fromEntity(authUser);
                }
        );
    }
}
