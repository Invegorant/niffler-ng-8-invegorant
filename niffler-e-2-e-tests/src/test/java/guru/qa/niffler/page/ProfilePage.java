package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class ProfilePage extends AbstractPage {

    private final SelenideElement uploadNewPictureBtn = $("label[class='image__input-label']");
    private final SelenideElement usernameInput = $("input[name='username']");
    private final SelenideElement nameInput = $("input[name='name']");
    private final SelenideElement saveChangesBtn = $("button[type='submit']");
    private final SelenideElement showArchivedSwitcher = $("input[type='checkbox']");
    private final SelenideElement categoryInput = $("input[name='category']");
    private final ElementsCollection categoryLabels = $$("span[class*='MuiChip-labelMedium']");
    private final ElementsCollection categoryEditBtns = $$("button[aria-label='Edit category']");
    private final ElementsCollection categoryArchiveBtns = $$("button[aria-label='Archive category']");

    private SelenideElement getCategoryButton(String category) {
        return categoryLabels.find(text(category));
    }

    private SelenideElement getCategoryRow(String category) {
        return categoryEditBtns.find(text(category)).parent().parent();
    }

    private SelenideElement getEditCategoryButton(String category) {
        return getCategoryRow(category).find(String.valueOf(categoryEditBtns));
    }

    private SelenideElement getArchiveCategoryButton(String category) {
        return getCategoryRow(category).find(String.valueOf(categoryArchiveBtns));
    }

    public ProfilePage setUsername(String username) {
        usernameInput.setValue(username);
        return this;
    }

    public ProfilePage setName(String name) {
        nameInput.setValue(name);
        return this;
    }

    public ProfilePage saveChanges() {
        saveChangesBtn.click();
        return this;
    }

    public ProfilePage addCategory(String category) {
        categoryInput.setValue(category);
        categoryInput.pressEnter();
        return this;
    }

    public ProfilePage categoryIsPresent(String category) {
        getCategoryButton(category).shouldBe(visible);
        return this;
    }

    public ProfilePage categoryIsNotPresent(String category) {
        getCategoryButton(category).shouldNotBe(visible);
        return this;
    }

    public ProfilePage showArchivedCategories() {
        showArchivedSwitcher.should(exist).click();
        return this;
    }

    public ProfilePage assertToastMessage(String msg) {
        $(byText(msg)).shouldBe(visible);
        return this;
    }
}
