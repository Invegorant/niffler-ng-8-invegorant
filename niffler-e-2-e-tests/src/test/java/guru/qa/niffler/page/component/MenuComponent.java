package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class MenuComponent {

    private final ElementsCollection menuItems = $$("ul[role='menu'] li");
    private final SelenideElement menuBtn = $("button[aria-label='Menu']");

    @SneakyThrows
    public void selectMenuItem(MenuItem item) {
        menuItems.find(exactText(item.getMenuValue())).click();
    }

    @SneakyThrows
    public MenuComponent openMenu() {
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