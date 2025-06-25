package guru.qa.niffler.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DataFilterValues {
    ALL_TIME("All time"),
    TODAY("Today"),
    WEEK("last week"),
    MONTH("Last month");

    public final String value;
}
