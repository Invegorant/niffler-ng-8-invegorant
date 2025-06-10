package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.components.Header;
import guru.qa.niffler.page.components.MenuComponent;
import guru.qa.niffler.utils.ScreenDiffResult;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.Duration;

import static com.codeborne.selenide.ClickOptions.usingJavaScript;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.openqa.selenium.Keys.ENTER;

public class MainPage {

    private final Header header = new Header();
    private final SelenideElement historyOfSpendingsHeader = $(byText("History of Spendings"));
    private final SelenideElement statisticsHeader = $(byText("Statistics"));
    private final ElementsCollection tableRows = $$("#spendings tbody tr");
    private final SelenideElement searchInput = $("input");
    private final SelenideElement searchSpendingField = $("input[aria-label='search']");
    private final SelenideElement img = $("canvas[role='img']");
    private final ElementsCollection bubbles = $("#legend-container").$$("li");
    private final SelenideElement deleteBtn = $("#delete");
    private final SelenideElement popup = $("div[role='dialog']");

    public ProfilePage openProfilePage() {
        header.openMenu().selectMenuItem(MenuComponent.MenuItem.PROFILE);
        return new ProfilePage();
    }

    public FriendsPage openFriendsPage() {
        header.openMenu().selectMenuItem(MenuComponent.MenuItem.FRIENDS);
        return new FriendsPage();
    }

    public AllPeoplePage openAllPeoplePage() {
        header.openMenu().selectMenuItem(MenuComponent.MenuItem.ALL_PEOPLE);
        return new AllPeoplePage();
    }

    public EditSpendingPage editSpending(String spendingDescription) {
        tableRows.find(text(spendingDescription))
                .$$("td")
                .get(5)
                .click();
        return new EditSpendingPage();
    }

    public void checkHistoryOfSpendingIsPresent() {
        historyOfSpendingsHeader.shouldBe(visible);
    }

    public MainPage checkStatisticsIsPresent() {
        statisticsHeader.shouldBe(visible);
        return this;
    }

    public void checkThatTableContainsSpending(String spendingDescription) {
        tableRows.find(text(spendingDescription))
                .should(visible);
    }

    public MainPage searchSpending(String spending) {
        searchInput.setValue(spending).pressEnter();
        return this;
    }

    public MainPage waitForPieChartToLoad() {
        img.is(image, Duration.ofSeconds(5));
        return this;
    }

    public MainPage checkBubblesHasText(String description) {
        bubbles.find(text(description))
                .should(visible);
        return this;
    }

    public void assertPicture(BufferedImage expected) throws IOException {
        Selenide.sleep(5000L);

        BufferedImage actual = ImageIO.read(
                img.screenshot()
        );
        assertFalse(new ScreenDiffResult(
                actual,
                expected
        ));
    }

    public MainPage deleteSpending(String description) {
        searchInField(description);
        SelenideElement row = tableRows.find(text(description));
        row.$$("td").get(0).click();
        deleteBtn.click();
        popup.$(byText("Delete")).click(usingJavaScript());
        return this;
    }

    public MainPage checkTableSize(int expectedSize) {
        tableRows.should(size(expectedSize));
        return this;
    }

    private void searchInField(String description) {
        searchSpendingField.clear();
        searchSpendingField.setValue(description).sendKeys(ENTER);
    }
}
