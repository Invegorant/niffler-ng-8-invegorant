package guru.qa.niffler.page.component;

import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.Month;
import java.util.Date;
import java.util.Locale;

import static com.codeborne.selenide.Condition.exactOwnText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class Calendar extends BaseComponent<Calendar> {

    private final String previousMonthButton = "button[title='Previous month']";
    private final String nextMonthButton = "button[title='Next month']";
    private final String yearsContainer = "div.MuiYearCalendar-root";
    private final String daysContainer = "div.MuiDayCalendar-monthContainer";
    private final String monthAndYearLabel = "div.MuiPickersCalendarHeader-label";
    private final String selectYearButton = "button[aria-label='calendar view is open, switch to year view']";

    public Calendar() {
        super($("div.MuiDateCalendar-root"));
    }

    @Step("Set date with calendar")
    @Nonnull
    public Calendar setDate(Date date) {
        setYear(date.getYear());
        setMonth(date.getMonth());
        setDay(date.getDate());
        return this;
    }

    @Step("Set year with calendar")
    @Nonnull
    public Calendar setYear(int year) {
        self.$(selectYearButton).click();
        self.$(yearsContainer)
                .$$("button")
                .findBy(text(String.valueOf(year + 1900)))
                .click();
        return this;
    }

    @Step("Set month with calendar")
    @Nonnull
    public Calendar setMonth(int targetMonth) {
        String currentMonthName = self.$(monthAndYearLabel).getText().split(" ")[0];
        Month currentMonthEnum = Month.valueOf(currentMonthName.toUpperCase(Locale.ENGLISH));
        int monthIndex = currentMonthEnum.getValue() - 1;
        while (monthIndex != targetMonth) {
            if (monthIndex < targetMonth) {
                self.$(nextMonthButton).click();
                monthIndex++;
            } else {
                self.$(previousMonthButton).click();
                monthIndex--;
            }
        }
        return this;
    }

    @Step("Set day with calendar")
    @Nonnull
    public Calendar setDay(int day) {
        self.$(daysContainer)
                .$$("button")
                .findBy(exactOwnText(String.valueOf(day)))
                .click();
        return this;
    }
}