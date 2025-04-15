package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.Test;

public class SpendingTest extends AbstractTest {

    @Spend(
            username = DEFAULT_USERNAME,
            category = "Обучение",
            description = "Обучение Niffler 2.0",
            amount = 89000.00,
            currency = CurrencyValues.RUB
    )
    @Test
    void spendingDescriptionShouldBeUpdatedByTableAction(SpendJson spend) {
        final String newDescription = "Обучение Niffler NG";

        openLoginPage()
                .doLogin(DEFAULT_USERNAME, DEFAULT_PASSWORD)
                .editSpending(spend.description())
                .editDescription(newDescription);

        new MainPage().checkThatTableContains(newDescription);
    }
}
