package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.utils.ScreenDiffResult;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertFalse;

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

    @User(
            spendings = @Spending(
                    category = "Обучение",
                    description = "Обучение Advanced 2.0",
                    amount = 79990,
                    currency = CurrencyValues.RUB
            )
    )
    @ScreenShotTest("img/expected-stat.png")
    void checkStatComponentTest(UserJson user, BufferedImage expected) throws IOException {
        openLoginPage()
                .doLogin(user.username(), (user.testData().password()));

        Selenide.sleep(5000L);

        BufferedImage actual = ImageIO.read($("canvas[role='img']").screenshot());
        assertFalse(new ScreenDiffResult(
                expected,
                actual
        ));
    }
}
