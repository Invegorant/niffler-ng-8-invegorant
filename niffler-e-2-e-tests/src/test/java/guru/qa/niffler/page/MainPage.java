package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.Header;
import guru.qa.niffler.page.component.SpendsTable;
import guru.qa.niffler.page.component.StatComponent;
import io.qameta.allure.Step;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.Duration;

import static com.codeborne.selenide.Condition.image;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class MainPage extends BasePage<MainPage> {

    @Getter
    private final Header header = new Header();
    @Getter
    private final SpendsTable spendsTable = new SpendsTable($("#spendings"));
    @Getter
    private final StatComponent statComponent = new StatComponent($("#stat"));

    private final SelenideElement historyOfSpendingsHeader = $(byText("History of Spendings"));
    private final SelenideElement statisticsHeader = $(byText("Statistics"));
    private final SelenideElement img = $("canvas[role='img']");

    @Step("Verify 'History of Spendings' is displayed")
    public void checkHistoryOfSpendingIsPresent() {
        historyOfSpendingsHeader.shouldBe(visible);
    }

    @Step("Verify 'Statistics' is displayed")
    @Nonnull
    public MainPage checkStatisticsIsPresent() {
        statisticsHeader.shouldBe(visible);
        return this;
    }

    @Step("Wait for Pie Chart to load")
    @Nonnull
    public MainPage waitForPieChartToLoad() {
        img.is(image, Duration.ofSeconds(5));
        return this;
    }

    public void checkMainPageIsOpened() {
        checkStatisticsIsPresent();
    }
}
