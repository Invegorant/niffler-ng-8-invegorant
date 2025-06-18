package guru.qa.niffler.model;


import com.codeborne.selenide.SelenideConfig;

public enum Browser {
    CHROME,
    FIREFOX;

    public SelenideConfig config() {
        return new SelenideConfig()
                .browser(this.name().toLowerCase())
                .pageLoadStrategy("eager")
                .timeout(5000L);
    }

}
