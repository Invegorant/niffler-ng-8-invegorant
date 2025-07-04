package guru.qa.niffler.test.web.alert;

import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.test.web.AbstractTest;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.common.messages.ApplicationWarnings.*;
import static guru.qa.niffler.model.CurrencyValues.RUB;

@WebTest
@Feature("Аллерты - Spendings")
public class SpendAlertTest extends AbstractTest {

    @User
    @Test
    void spendingAlert_shouldDisplayAlertWhenSpendingIsAdded(UserJson user) {
        SpendJson spend = user.testData().spendings().getFirst();
        login(user)
                .getHeader()
                .navigateToAddSpendingPage()
                .fillAllFields(spend)
                .clickConfirmButton()
                .checkAlertMessage(SPEND_CREATED.getVal());
    }

    @User(
            spendings = {
                    @Spending(
                            category = "Grocery",
                            description = "Bread",
                            amount = 100.56,
                            currency = RUB
                    )
            }
    )
    @Test
    void spendingAlert_shouldDisplayAlertWhenSpendingDeleted(UserJson user) {
        login(user)
                .getSpendsTable()
                .deleteSpend(user.testData().spendings().getFirst().description())
                .checkAlertMessage(SPEND_DELETED.getVal());
    }

    @User(
            spendings = {
                    @Spending(
                            category = "Grocery",
                            description = "Bread",
                            amount = 100.56,
                            currency = RUB
                    )
            }
    )
    @Test
    void spendingAlert_shouldDisplayAlertWhenSpendingUpdated(UserJson user) {
        login(user)
                .getSpendsTable()
                .editSpend(user.testData().spendings().getFirst().description())
                .editAmount("2000")
                .checkAlertMessage(SPEND_UPDATED.getVal());
    }
}
