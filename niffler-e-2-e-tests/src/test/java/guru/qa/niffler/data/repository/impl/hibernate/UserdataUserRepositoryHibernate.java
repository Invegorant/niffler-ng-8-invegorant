package guru.qa.niffler.data.repository.impl.hibernate;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import io.qameta.allure.Step;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jpa.EntityManagers.em;

@ParametersAreNonnullByDefault
public class UserdataUserRepositoryHibernate implements UserdataUserRepository {

    private static final Config CFG = Config.getInstance();

    private final EntityManager em = em(CFG.userdataJdbcUrl());

    @Step("Create user")
    @NotNull
    @Override
    public UserEntity createUser(UserEntity user) {
        em.joinTransaction();
        em.persist(user);
        return user;
    }

    @Step("Find user by id: {id}")
    @NotNull
    @Override
    public Optional<UserEntity> findById(UUID id) {
        return Optional.ofNullable(
                em.find(UserEntity.class, id)
        );
    }

    @Step("Find user by username: {username}")
    @NotNull
    @Override
    public Optional<UserEntity> findByUsername(String username) {
        try {
            return Optional.of(
                    em.createQuery("select u from UserEntity u where u.username =: username", UserEntity.class)
                            .setParameter("username", username)
                            .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Step("Update user")
    @NotNull
    @Override
    public UserEntity update(UserEntity user) {
        em.joinTransaction();
        em.merge(user);
        return user;
    }

    @Step("Send invitation")
    @Override
    public void sendInvitation(UserEntity requester, UserEntity addressee) {
        em.joinTransaction();
        addressee.addInvitations(requester);
    }

    @Step("Add friend")
    @Override
    public void addFriend(UserEntity requester, UserEntity addressee) {
        em.joinTransaction();
        requester.addFriends(FriendshipStatus.ACCEPTED, addressee);
        addressee.addFriends(FriendshipStatus.ACCEPTED, requester);
    }

    @Step("Remove friend")
    @Override
    public void removeFriend(UserEntity user) {
        em.joinTransaction();
        UserEntity managedUser = em.find(UserEntity.class, user.getId());
        if (managedUser != null) {
            em.remove(managedUser);
        }
    }
}
