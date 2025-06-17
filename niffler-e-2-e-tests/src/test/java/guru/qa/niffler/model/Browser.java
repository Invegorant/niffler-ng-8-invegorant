package guru.qa.niffler.model;


import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.test.web.utils.SelenideUtils;

public enum Browser {
    CHROME(SelenideUtils.chromeConfig),
    FIREFOX(SelenideUtils.firefoxConfig);

    private final SelenideConfig config;

    Browser(SelenideConfig config) {
        this.config = config;
    }

    public SelenideDriver createDriver() {
        return new SelenideDriver(this.config);
    }
}
