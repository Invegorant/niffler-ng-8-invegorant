package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.jdbc.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.jdbc.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpendDbClient {

    private static final Config CFG = Config.getInstance();

    private final CategoryDao categoryDao = new CategoryDaoJdbc();
    private final SpendDao spendDao = new SpendDaoJdbc();

    private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(
            CFG.spendJdbcUrl()
    );

    public SpendJson createSpend(SpendJson spend) {
        return jdbcTxTemplate.execute(() -> {
                    SpendEntity spendEntity = SpendEntity.fromJson(spend);
                    if (spendEntity.getCategory().getId() == null) {
                        CategoryEntity categoryEntity = categoryDao.createCategory(spendEntity.getCategory());
                        spendEntity.setCategory(categoryEntity);
                    }
                    return SpendJson.fromEntity(
                            spendDao.createSpend(spendEntity)
                    );
                }
        );
    }

    public Optional<SpendJson> findSpendById(UUID id) {
        return jdbcTxTemplate.execute(() -> spendDao.findSpendById(id)
                .map(spendEntity -> {
                    categoryDao.findCategoryById(spendEntity.getCategory().getId())
                            .ifPresent(spendEntity::setCategory);
                    return SpendJson.fromEntity(spendEntity);
                })
        );
    }

    public List<SpendJson> findAllSpendsByUsername(String username) {
        return jdbcTxTemplate.execute(() -> spendDao.findAllByUsername(username).stream().map(se -> {
                    categoryDao.findCategoryById(se.getCategory().getId()).ifPresent(se::setCategory);
                    return SpendJson.fromEntity(se);
                }).toList()
        );
    }


    public void deleteSpend(SpendJson spend) {
        jdbcTxTemplate.execute(() -> spendDao.deleteSpend(SpendEntity.fromJson(spend)));
    }

    public CategoryJson createCategory(CategoryJson category) {
        return jdbcTxTemplate.execute(() -> {
                    CategoryEntity ce = CategoryEntity.fromJson(category);
                    return CategoryJson.fromEntity(categoryDao.createCategory(ce));
                }
        );
    }

    public Optional<CategoryJson> findCategoryById(UUID id) {
        return jdbcTxTemplate.execute(() -> categoryDao
                .findCategoryById(id)
                .map(CategoryJson::fromEntity)
        );
    }

    public Optional<CategoryJson> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        return jdbcTxTemplate.execute(() -> categoryDao
                .findCategoryByUsernameAndCategoryName(username, categoryName)
                .map(CategoryJson::fromEntity)
        );
    }

    public List<CategoryJson> findAllCategoriesByUsername(String username) {
        return jdbcTxTemplate.execute(() -> categoryDao.findAllByUsername(username)
                .stream()
                .map(CategoryJson::fromEntity)
                .toList()
        );
    }

    public void deleteCategory(CategoryJson category) {
        jdbcTxTemplate.execute(() -> categoryDao.deleteCategory(CategoryEntity.fromJson(category)));
    }
}
