package guru.qa.niffler.utils;

import com.github.javafaker.Faker;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class RandomDataUtils {

    private static final Faker FAKER = new Faker();

    @Nonnull
    public static String randomUsername() {
        return FAKER.name().username();
    }

    @Nonnull
    public static String randomName() {
        return FAKER.name().firstName();
    }

    @Nonnull
    public static String randomSurname() {
        return FAKER.name().lastName();
    }

    @Nonnull
    public static String randomCategoryName() {
        return FAKER.name().lastName();
    }

    @Nonnull
    public static String randomSentence(int wordsCount) {
        return FAKER.lorem().sentence(wordsCount);
    }

    @Nonnull
    public static int randomCount() {
        return FAKER.random().nextInt(1, 10);
    }
}
