package guru.qa.niffler.page.components;

import com.codeborne.selenide.ElementsCollection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.$$;

public class MenuComponent {

    private final ElementsCollection menuItems = $$("ul[role='menu'] li");

    @SneakyThrows
    public void selectMenuItem(MenuItem item) {
        menuItems.find(exactText(item.getMenuValue())).click();
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