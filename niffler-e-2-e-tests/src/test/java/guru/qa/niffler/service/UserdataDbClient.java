package guru.qa.niffler.service;

import guru.qa.niffler.data.dao.UserdataDao;
import guru.qa.niffler.data.dao.impl.UserdataDaoJdbc;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.UserJson;

import java.util.Optional;
import java.util.UUID;

public class UserdataDbClient {

    private final UserdataDao userdataDao = new UserdataDaoJdbc();

    public UserJson createUser(UserEntity user) {
        return UserJson.fromEntity(userdataDao.createUser(user));
    }

    public Optional<UserJson> findUserByUsername(String username) {
        return userdataDao.findByUsername(username).map(UserJson::fromEntity);
    }

    public Optional<UserJson> findUserById(UUID id) {
        return userdataDao.findById(id).map(UserJson::fromEntity);
    }

    public void deleteUser(UserEntity user) {
        userdataDao.deleteUser(user);
    }
}
