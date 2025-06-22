package guru.qa.niffler.data.repository.impl.hibernate;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jpa.EntityManagers.em;

@ParametersAreNonnullByDefault
public class AuthUserRepositoryHibernate implements AuthUserRepository {

    private static final Config CFG = Config.getInstance();

    private final EntityManager em = em(CFG.authJdbcUrl());

    @NotNull
    @Override
    public AuthUserEntity create(AuthUserEntity user) {
        em.joinTransaction();
        em.persist(user);
        return user;
    }

    @NotNull
    @Override
    public AuthUserEntity update(AuthUserEntity user) {
        em.joinTransaction();
        em.merge(user);
        return user;
    }

    @NotNull
    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        return Optional.ofNullable(
                em.find(AuthUserEntity.class, id)
        );
    }

    @NotNull
    @Override
    public Optional<AuthUserEntity> findByUsername(String username) {
        try {
            return Optional.of(
                    em.createQuery("SELECT u FROM AuthUserEntity u WHERE u.username =: username",
                                    AuthUserEntity.class)
                            .setParameter("username", username)
                            .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public void remove(AuthUserEntity user) {
        em.joinTransaction();
        AuthUserEntity userEntity = em.contains(user)
                ? user
                : em.merge(user);
        em.remove(userEntity);
    }
}
