package guru.qa.niffler.model;

import guru.qa.niffler.condition.Color;

public record Bubble(
        Color color,
        String text
) {
    @Override
    public String toString() {
        return "Bubble: {color = %s, text = %s}".formatted(color, text);
    }
}
