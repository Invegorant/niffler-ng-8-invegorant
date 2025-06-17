package guru.qa.niffler.test.web.utils;

import com.codeborne.selenide.SelenideConfig;

public class SelenideUtils {

    public static final SelenideConfig chromeConfig = new SelenideConfig()
            .browser("chrome")
            .pageLoadStrategy("eager")
            .timeout(5000L);

    public static final SelenideConfig firefoxConfig = new SelenideConfig()
            .browser("firefox")
            .pageLoadStrategy("eager")
            .timeout(5000L);
}
