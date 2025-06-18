package guru.qa.niffler.test.web.utils;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.model.Browser;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;

public class BrowserConverterUtils implements ArgumentConverter {

    @Override
    public SelenideDriver convert(Object source, ParameterContext context) throws ArgumentConversionException {
        if (!(source instanceof Browser)) {
            throw new ArgumentConversionException("Cannot convert argument");
        }
        try {
            Browser browser = (Browser) source;
            return new SelenideDriver(browser.config());
        } catch (IllegalArgumentException e) {
            throw new ArgumentConversionException("Failed to convert argument to Browser enum: " + source, e);
        }
    }
}
