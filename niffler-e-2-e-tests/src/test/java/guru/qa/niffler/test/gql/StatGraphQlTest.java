package guru.qa.niffler.test.gql;

import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.StatQuery;
import guru.qa.UpdateCategoryMutation;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.rest.CategoryJson;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.type.CategoryInput;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatGraphQlTest extends BaseGraphQlTest {

    private static final Map<CurrencyValues, Double> CURRENCY_RATES = Map.of(
            CurrencyValues.USD, 66.66667,
            CurrencyValues.EUR, 72.0,
            CurrencyValues.RUB, 1.0
    );

    @Test
    @ApiLogin
    @User
    void allStatShouldBeReturnedFromGateway(@Token String bearerToken) {
        StatQuery.Stat result = getStatistic(bearerToken);
        assertEquals(0.0, result.total);
    }

    @Test
    @ApiLogin
    @User(spendings = {
            @Spending(
                    category = "Обучение",
                    description = "Обучение Advanced 2.0",
                    amount = 1000,
                    currency = CurrencyValues.USD),
            @Spending(
                    category = "Магазины",
                    description = "Продукты",
                    amount = 100,
                    currency = CurrencyValues.EUR)
    })
    void checkStatWithDifferentCurrencies(@Token String bearerToken, UserJson user) {
        StatQuery.Stat result = getStatistic(bearerToken);

        List<SpendJson> expectedSpends = getExpectedSpends(user);
        List<SpendJson> actualSpends = getActualSpends(result, user);

        assertThat(actualSpends)
                .usingRecursiveComparison()
                .ignoringFields("category.id", "spendDate")
                .isEqualTo(expectedSpends);

        assertEquals(calculateExpectedTotal(user), result.total, 0.001);
    }

    @Test
    @ApiLogin
    @User(spendings = {
            @Spending(
                    category = "Clothes",
                    description = "jeans",
                    amount = 200,
                    currency = CurrencyValues.USD),
            @Spending(
                    category = "Sports",
                    description = "bike",
                    amount = 2000,
                    currency = CurrencyValues.EUR)
    })
    void checkStatWithArchivedCategory(@Token String bearerToken, UserJson user) {
        CategoryJson categoryToArchive = user.testData().spendings().getFirst().category();
        archiveCategory(bearerToken, categoryToArchive);

        StatQuery.Stat result = getStatistic(bearerToken);

        List<SpendJson> expectedSpends = getExpectedSpends(user);
        List<SpendJson> actualSpends = getActualSpends(result, user);

        assertThat(actualSpends)
                .usingRecursiveComparison()
                .ignoringFields("category.id", "spendDate", "category.name")
                .isEqualTo(expectedSpends);

        assertEquals("Archived", actualSpends.getFirst().category().name());
        assertEquals(calculateExpectedTotal(user), result.total, 0.01);
    }


    private StatQuery.Stat getStatistic(String bearerToken) {
        return Rx2Apollo.single(apolloClient.query(StatQuery.builder().build())
                        .addHttpHeader("authorization", bearerToken))
                .blockingGet()
                .dataOrThrow()
                .stat;
    }

    private UpdateCategoryMutation.Category archiveCategory(String bearerToken, CategoryJson category) {
        return Rx2Apollo.single(apolloClient.mutation(UpdateCategoryMutation.builder()
                                .input(CategoryInput.builder()
                                        .id(category.id().toString())
                                        .name(category.name())
                                        .archived(true)
                                        .build())
                                .build())
                        .addHttpHeader("authorization", bearerToken))
                .blockingGet()
                .dataOrThrow()
                .category;
    }

    private SpendJson convertToRubSpend(SpendJson spend) {
        return new SpendJson(
                null,
                spend.spendDate(),
                spend.category(),
                CurrencyValues.RUB,
                roundToTwoDecimals(spend.amount() * CURRENCY_RATES.get(spend.currency())),
                "",
                spend.username()
        );
    }

    private SpendJson mapStatToSpend(StatQuery.StatByCategory stat, UserJson user) {
        return new SpendJson(
                null,
                stat.firstSpendDate,
                new CategoryJson(null, stat.categoryName, user.username(), false),
                CurrencyValues.valueOf(stat.currency.rawValue),
                roundToTwoDecimals(stat.sum),
                "",
                user.username()
        );
    }

    private double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private List<SpendJson> getExpectedSpends(UserJson user) {
        return user.testData().spendings().stream()
                .map(this::convertToRubSpend)
                .sorted(Comparator.comparing(spend -> spend.category().name()))
                .toList();
    }

    private List<SpendJson> getActualSpends(StatQuery.Stat result, UserJson user) {
        return result.statByCategories.stream()
                .map(stat -> mapStatToSpend(stat, user))
                .sorted(Comparator.comparing(spend -> spend.category().name()))
                .toList();
    }

    private double calculateExpectedTotal(UserJson user) {
        return user.testData().spendings().stream()
                .mapToDouble(spend -> spend.amount() * CURRENCY_RATES.get(spend.currency()))
                .sum();
    }
}