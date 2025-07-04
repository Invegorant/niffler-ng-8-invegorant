package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.spend.SpendEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface SpendDao {

    @Nonnull
    SpendEntity createSpend(SpendEntity spend);

    @Nonnull
    Optional<SpendEntity> findSpendById(UUID id);

    @Nonnull
    List<SpendEntity> findAllByUsername(String username);

    @Nonnull
    List<SpendEntity> findAll();

    boolean deleteSpend(SpendEntity spend);
}
