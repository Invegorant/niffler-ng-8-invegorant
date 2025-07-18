package guru.qa.niffler.jupiter.annotation.meta;

import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.Spending;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface User {
    Category[] categories() default {};

    Spending[] spendings() default {};

    String username() default "";

    int friends() default 0;

    int incomeInvitations() default 0;

    int outcomeInvitations() default 0;
}
