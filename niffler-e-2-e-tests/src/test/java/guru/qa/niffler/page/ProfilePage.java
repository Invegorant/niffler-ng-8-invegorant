package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.jupiter.extension.ScreenShotTestExtension;
import guru.qa.niffler.utils.ScreenDiffResult;
import org.openqa.selenium.By;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ParametersAreNonnullByDefault
public class ProfilePage extends BasePage<ProfilePage> {

    private final SelenideElement uploadNewPictureBtn = $("label[class='image__input-label']");
    private final SelenideElement usernameInput = $("input[name='username']");
    private final SelenideElement nameInput = $("input[name='name']");
    private final SelenideElement saveChangesBtn = $("button[type='submit']");
    private final SelenideElement showArchivedSwitcher = $("input[type='checkbox']");
    private final SelenideElement categoryInput = $("input[name='category']");
    private final ElementsCollection categoryLabels = $$("span[class*='MuiChip-labelMedium']");
    private final ElementsCollection categoryEditBtns = $$("button[aria-label='Edit category']");
    private final ElementsCollection categoryArchiveBtns = $$("button[aria-label='Archive category']");
    private final SelenideElement photoInput = $("input[type='file']");
    private final SelenideElement profilePic = $(By.xpath("//img[contains(@src, 'data:image/png')]"));
    private final ElementsCollection bubbles = $$(".MuiChip-filled.MuiChip-colorPrimary");
    private final ElementsCollection bubblesArchived = $$(".MuiChip-filled.MuiChip-colorDefault");

    @Nonnull
    private SelenideElement getCategoryButton(String category) {
        return categoryLabels.find(text(category));
    }

    @Nonnull
    private SelenideElement getCategoryRow(String category) {
        return categoryEditBtns.find(text(category)).parent().parent();
    }

    @Nonnull
    private SelenideElement getEditCategoryButton(String category) {
        return getCategoryRow(category).find(String.valueOf(categoryEditBtns));
    }

    @Nonnull
    private SelenideElement getArchiveCategoryButton(String category) {
        return getCategoryRow(category).find(String.valueOf(categoryArchiveBtns));
    }

    @Nonnull
    public ProfilePage setUsername(String username) {
        usernameInput.setValue(username);
        return this;
    }

    @Nonnull
    public ProfilePage setName(String name) {
        nameInput.setValue(name);
        return this;
    }

    @Nonnull
    public ProfilePage saveChanges() {
        saveChangesBtn.click();
        return this;
    }

    @Nonnull
    public ProfilePage addCategory(String category) {
        categoryInput.setValue(category);
        categoryInput.pressEnter();
        return this;
    }

    @Nonnull
    public ProfilePage categoryIsPresent(String category) {
        getCategoryButton(category).shouldBe(visible);
        return this;
    }

    @Nonnull
    public ProfilePage categoryIsNotPresent(String category) {
        getCategoryButton(category).shouldNotBe(visible);
        return this;
    }

    @Nonnull
    public ProfilePage showArchivedCategories() {
        showArchivedSwitcher.should(exist).click();
        return this;
    }

    @Nonnull
    public ProfilePage assertToastMessage(String msg) {
        $(byText(msg)).shouldBe(visible);
        return this;
    }

    @Nonnull
    public ProfilePage submitProfile() {
        saveChangesBtn.click();
        return this;
    }

    @Nonnull
    public ProfilePage uploadPhoto(String path) {
        photoInput.uploadFromClasspath(path);
        return this;
    }

    public void assertProfilePic(BufferedImage expected) throws IOException {
        Selenide.sleep(3000L);

        BufferedImage actual = ImageIO.read(Objects.requireNonNull(profilePic.screenshot()));
        assertFalse(new ScreenDiffResult(
                        actual,
                        expected
                ),
                ScreenShotTestExtension.ASSERT_SCREEN_MESSAGE
        );
    }

    @Nonnull
    public ProfilePage updateCategory(String category) {
        SelenideElement row = bubbles.find(text(category));
        row.sibling(0).$("button[aria-label='Archive category']").click();
        $(By.xpath("//button[text() = 'Archive']")).shouldBe(visible).click();
        return this;
    }
}
