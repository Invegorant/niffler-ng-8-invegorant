package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.Bubble;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;

@WebTest
public class SpendingTest extends AbstractTest {

    @Test
    @ApiLogin
    @User(
            spendings = @Spending(
                    category = "Обучение",
                    description = "Обучение Niffler 2.0",
                    amount = 89000.00,
                    currency = CurrencyValues.RUB
            )
    )
    void spending_spendingDescriptionShouldBeUpdatedByTableAction(UserJson user) {
        final String newDescription = "Обучение Niffler NG";
        MainPage mainPage = new MainPage();

        mainPage.getSpendsTable()
                .editSpend(user.testData().spendings().getFirst().description())
                .editDescription(newDescription);

        mainPage.getSpendsTable()
                .checkThatTableContains(user.testData().spendings().getFirst());
    }

    @ScreenShotTest("img/expected-stats.png")
    @ApiLogin
    @User(
            spendings = {@Spending
                    (
                            category = "Обучение",
                            description = "Обучение Advanced 2.0",
                            amount = 50000,
                            currency = CurrencyValues.RUB
                    ),
                    @Spending
                            (
                                    category = "Путешествие",
                                    description = "Путешествие в Сочи",
                                    amount = 80000,
                                    currency = CurrencyValues.RUB
                            )
            }
    )
    void checkStatComponentTest(BufferedImage expected) throws IOException {
        new MainPage()
                .getStatComponent()
                .checkStatisticImage(expected)
                .checkStatBubblesInAnyOrder(
                        new Bubble(Color.green, "Обучение 50000 ₽"),
                        new Bubble(Color.yellow, "Путешествие 80000 ₽"));
    }

    @Test
    @ApiLogin
    @User(
            spendings = {@Spending(
                    category = "Еда",
                    description = "Сырки Б Ю Александров",
                    amount = 1250.00,
                    currency = CurrencyValues.RUB
            ), @Spending(
                    category = "Обучение",
                    description = "Английский язык",
                    amount = 1300.00,
                    currency = CurrencyValues.RUB
            )})
    void checkSpendingTableTest(UserJson user) {
        new MainPage()
                .getStatComponent()
                .checkSpendTable(user.testData().spendings().toArray(SpendJson[]::new));
    }

    @ScreenShotTest(value = "img/expected-stat-archived.png")
    @ApiLogin
    @User(
            categories = {
                    @Category(name = "Поездки"),
                    @Category(name = "Ремонт", archived = true),
                    @Category(name = "Страховка", archived = true)
            },
            spendings = {
                    @Spending(
                            category = "Поездки",
                            description = "В Москву",
                            amount = 9500,
                            currency = CurrencyValues.RUB
                    ),
                    @Spending(
                            category = "Ремонт",
                            description = "Цемент",
                            amount = 100,
                            currency = CurrencyValues.RUB
                    ),
                    @Spending(
                            category = "Страховка",
                            description = "ОСАГО",
                            amount = 3000,
                            currency = CurrencyValues.RUB
                    )
            }
    )
    void statComponentShouldDisplayArchivedCategories(BufferedImage expected) throws IOException {
        new MainPage()
                .getStatComponent()
                .checkStatisticBubblesContains("Поездки 9500 ₽", "Archived 3100 ₽")
                .checkStatisticImage(expected)
                .checkBubbles(Color.yellow, Color.green);
    }

    @ScreenShotTest("img/empty-stat.png")
    @ApiLogin
    @User(
            spendings = @Spending(
                    category = "Обучение",
                    description = "Обучение Advanced 2.0",
                    amount = 79990,
                    currency = CurrencyValues.RUB
            )
    )
    void spending_shouldUpdateStatAfterSpendingIsRemoved(BufferedImage expected) throws IOException {
        MainPage mainPage = new MainPage();

        mainPage.getSpendsTable()
                .deleteSpend("Обучение Advanced 2.0");

        Selenide.refresh();

        mainPage.getStatComponent()
                .checkStatisticImage(expected);
    }

    @ScreenShotTest("img/expected-updated-stat.png")
    @ApiLogin
    @User(
            spendings = @Spending(
                    category = "Обучение",
                    description = "Обучение Advanced 2.0",
                    amount = 79990, currency = CurrencyValues.RUB
            )
    )
    void spending_shouldUpdateStatAfterSpendingIsUpdated(BufferedImage expected) throws IOException {
        final String newAmount = "50000";
        MainPage mainPage = new MainPage();

        mainPage.getSpendsTable()
                .editSpend("Обучение Advanced 2.0")
                .editAmount(newAmount);

        mainPage
                .waitForPieChartToLoad()
                .getStatComponent()
                .checkBubblesHasText("Обучение " + newAmount);
        mainPage.getStatComponent()
                .checkStatisticImage(expected);
    }

    @ScreenShotTest("img/expected-stats.png")
    @ApiLogin
    @User(
            spendings = @Spending(
                    category = "Обучение",
                    description = "Обучение Advanced 2.0",
                    amount = 50000,
                    currency = CurrencyValues.RUB
            )
    )
    void spending_shouldUpdateStatAfterCategoryIsArchived(BufferedImage expected) throws IOException {
        MainPage mainPage = new MainPage();

        mainPage.getHeader().toProfilePage()
                .updateCategory("Обучение");

        mainPage
                .waitForPieChartToLoad()
                .getStatComponent()
                .checkBubblesHasText("Archived " + "50000");

        mainPage.getStatComponent()
                .checkStatisticImage(expected);
    }

    @ScreenShotTest(
            value = "img/expected-stats.png",
            rewriteExpected = true
    )
    @ApiLogin
    @User(
            spendings = @Spending(
                    category = "Обучение",
                    description = "Обучение Advanced 2.0",
                    amount = 50000,
                    currency = CurrencyValues.RUB
            )
    )
    void spending_overwriteScreenshotTest(BufferedImage expected) throws IOException {
        new MainPage()
                .waitForPieChartToLoad()
                .getStatComponent()
                .checkStatisticImage(expected);
    }

    @Test
    @ApiLogin
    @User
    void spending_shouldSuccessfullyAddNewSpending(UserJson user) {
        SpendJson spend = user.testData().spendings().getFirst();
        new MainPage()
                .getHeader()
                .navigateToAddSpendingPage()
                .fillAllFields(spend)
                .clickConfirmButton()
                .getSpendsTable()
                .checkThatTableContains(spend);
    }
}
