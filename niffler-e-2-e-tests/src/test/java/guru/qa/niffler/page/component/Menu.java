package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@ParametersAreNonnullByDefault
public class Menu {

    private final ElementsCollection menuItems = $$("ul[role='menu'] li");
    private final SelenideElement menuBtn = $("button[aria-label='Menu']");

    @Step("Select '{item.name}' from menu")
    public void selectMenuItem(MenuItem item) {
        menuItems.find(exactText(item.getMenuValue())).click();
    }

    @Step("Open Menu")
    @Nonnull
    public Menu openMenu() {
        menuBtn.click();
        return this;
    }

    @Getter
    @AllArgsConstructor
    public enum MenuItem {
        PROFILE("Profile"),
        FRIENDS("Friends"),
        ALL_PEOPLE("All People"),
        SIGN_OUT("Sign out");

        private final String menuValue;
    }
}