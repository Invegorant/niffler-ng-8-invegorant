package guru.qa.niffler.page.components;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class Header {

    private final SelenideElement nifflerHref = $("[href='/main']");
    private final SelenideElement newSpendingBtn = $("[href='/spending']");
    private final SelenideElement menuBtn = $("button[aria-label='Menu']");
    private final MenuComponent menuComponent = new MenuComponent();

    public MenuComponent openMenu() {
        menuBtn.click();
        return menuComponent;
    }
}
