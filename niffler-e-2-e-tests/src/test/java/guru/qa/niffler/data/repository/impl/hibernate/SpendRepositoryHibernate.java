package guru.qa.niffler.data.repository.impl.hibernate;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jpa.EntityManagers.em;

public class SpendRepositoryHibernate implements SpendRepository {

    private static final Config CFG = Config.getInstance();

    private final EntityManager em = em(CFG.spendJdbcUrl());

    @Override
    public SpendEntity createSpend(SpendEntity spend) {
        em.joinTransaction();
        em.persist(spend);
        return spend;
    }

    @Override
    public SpendEntity updateSpend(SpendEntity spend) {
        em.joinTransaction();
        em.merge(spend);
        return spend;
    }

    @Override
    public CategoryEntity createCategory(CategoryEntity category) {
        em.joinTransaction();
        em.persist(category);
        return category;
    }

    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        return Optional.ofNullable(
                em.find(CategoryEntity.class, id)
        );
    }

    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String name) {
        try {
            return Optional.of(em.createQuery(
                            "SELECT c FROM CategoryEntity c WHERE c.username = :username AND c.name = :name",
                            CategoryEntity.class)
                    .setParameter("username", username)
                    .setParameter("name", name)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<SpendEntity> findSpendById(UUID id) {
        return Optional.ofNullable(
                em.find(SpendEntity.class, id)
        );
    }

    @Override
    public Optional<SpendEntity> findByUsernameAndSpendDescription(String username, String description) {
        try {
            return Optional.of(em.createQuery(
                            "SELECT s FROM SpendEntity s WHERE s.username = :username AND s.description = :description",
                            SpendEntity.class)
                    .setParameter("username", username)
                    .setParameter("description", description)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public void removeSpend(SpendEntity spend) {
        em.joinTransaction();
        SpendEntity se = em.merge(spend);
        em.remove(se);
    }

    @Override
    public void removeCategory(CategoryEntity category) {
        em.joinTransaction();
        CategoryEntity ce = em.merge(category);
        em.remove(ce);
    }
}
