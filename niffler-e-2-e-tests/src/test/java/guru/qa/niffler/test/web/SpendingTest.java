package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.Test;

@WebTest
public class SpendingTest extends AbstractTest {

    @User(
            spendings = @Spending(
                    category = "Обучение",
                    description = "Обучение Niffler 2.0",
                    amount = 89000.00,
                    currency = CurrencyValues.RUB
            )
    )
    @Test
    void spendingDescriptionShouldBeUpdatedByTableAction(UserJson user) {
        final String newDescription = "Обучение Niffler NG";
        final String username = user.username();

        openLoginPage()
                .doLogin(username, (user.testData().password()))
                .searchRequestByUsername(username)
                .editSpending(user.testData().spendings().getFirst().description())
                .editDescription(newDescription);

        new MainPage()
                .searchRequestByUsername(username)
                .checkThatTableContainsSpending(newDescription);
    }
}
