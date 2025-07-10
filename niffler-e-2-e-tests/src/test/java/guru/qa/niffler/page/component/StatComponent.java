package guru.qa.niffler.page.component;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.jupiter.extension.ScreenShotTestExtension;
import guru.qa.niffler.model.Bubble;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.utils.ScreenDiffResult;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static guru.qa.niffler.condition.SpendConditions.spend;
import static guru.qa.niffler.condition.StatConditions.*;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ParametersAreNonnullByDefault
public class StatComponent extends BaseComponent<StatComponent> {

    private final SelenideElement self = $("#stat");
    private final ElementsCollection bubbles = self.$("#legend-container").$$("li");
    private final SelenideElement chart = $("canvas[role='img']");
    private final ElementsCollection statisticCells = $$("#legend-container li");
    private final ElementsCollection tableRows = $$("#spendings tbody tr");

    public StatComponent(SelenideElement self) {
        super(self);
    }

    @Step("Verify bubbles has text: {description}")
    @Nonnull
    public StatComponent checkBubblesHasText(String description) {
        bubbles.find(text(description))
                .should(visible);
        return this;
    }

    @Step("Verify bubbles contains texts")
    @Nonnull
    public StatComponent checkStatisticBubblesContains(String... texts) {
        bubbles.should(CollectionCondition.texts(texts));
        return this;
    }

    @Step("Verify statistic image")
    @Nonnull
    public StatComponent checkStatisticImage(BufferedImage expectedImage) throws IOException {
        Selenide.sleep(5000L);
        assertFalse(
                new ScreenDiffResult(
                        chartScreenshot(),
                        expectedImage
                ),
                ScreenShotTestExtension.ASSERT_SCREEN_MESSAGE
        );
        return this;
    }

    @Step("Get image from chart")
    @Nonnull
    public BufferedImage chartScreenshot() throws IOException {
        return ImageIO.read(requireNonNull(chart.screenshot()));
    }

    @Step("Check that stat bubbles contains colors {expectedColors}")
    @Nonnull
    public StatComponent checkBubbles(Color... expectedColors) {
        bubbles.should(color(expectedColors));
        return this;
    }

    @Step("Check stat bubbles")
    @Nonnull
    public StatComponent checkStatBubbles(Bubble... expectedBubbles) {
        statisticCells.shouldHave(bubble(expectedBubbles));
        return this;
    }

    @Step("Check stat bubbles in any order")
    @Nonnull
    public StatComponent checkStatBubblesInAnyOrder(Bubble... expectedBubbles) {
        statisticCells.shouldHave(bubblesInAnyOrder(expectedBubbles));
        return this;
    }

    @Step("Check stat bubbles contains")
    @Nonnull
    public StatComponent checkStatBubblesContains(Bubble... expectedBubbles) {
        statisticCells.shouldHave(bubblesContains(expectedBubbles));
        return this;
    }

    @Step("Check spending table")
    @Nonnull
    public StatComponent checkSpendTable(SpendJson... expectedSpends) {
        tableRows.shouldHave(spend(expectedSpends));
        return this;
    }
}
