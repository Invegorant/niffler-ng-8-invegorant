package guru.qa.niffler.test.web.alert;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.test.web.AbstractTest;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.common.messages.ApplicationWarnings.*;
import static guru.qa.niffler.model.CurrencyValues.RUB;

@WebTest
@Feature("Аллерты - Spendings")
public class SpendAlertTest extends AbstractTest {

    @Test
    @ApiLogin
    @User
    void spendingAlert_shouldDisplayAlertWhenSpendingIsAdded(UserJson user) {
        SpendJson spend = user.testData().spendings().getFirst();
        new MainPage()
                .getHeader()
                .navigateToAddSpendingPage()
                .fillAllFields(spend)
                .clickConfirmButton()
                .checkAlertMessage(SPEND_CREATED.getVal());
    }

    @Test
    @ApiLogin
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
    void spendingAlert_shouldDisplayAlertWhenSpendingDeleted(UserJson user) {
        new MainPage()
                .getSpendsTable()
                .deleteSpend(user.testData().spendings().getFirst().description())
                .checkAlertMessage(SPEND_DELETED.getVal());
    }

    @Test
    @ApiLogin
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
    void spendingAlert_shouldDisplayAlertWhenSpendingUpdated(UserJson user) {
        new MainPage()
                .getSpendsTable()
                .editSpend(user.testData().spendings().getFirst().description())
                .editAmount("2000")
                .checkAlertMessage(SPEND_UPDATED.getVal());
    }
}
