package guru.qa.niffler.common.messages;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApplicationWarnings {

    /**** SignupWarnings ****/
    USER_EXISTS("Username `%s` already exists"),
    INVALID_USERNAME("Allowed username length should be from 3 to 50 characters"),
    INVALID_PASSWORD("Allowed password length should be from 3 to 12 characters"),
    PASSWORDS_DO_NOT_MATCH("Passwords should be equal"),
    VALIDATION_MESSAGE("Please fill out this field."),

    /**** LoginWarnings ****/
    BAD_CREDENTIALS("Bad credentials"),

    /**** SpendPageAlertMessages ****/
    SPEND_CREATED("New spending is successfully created"),
    SPEND_DELETED("Spendings succesfully deleted"),
    SPEND_UPDATED("Spending is edited successfully"),


    /**** ProfileAlertMessages ****/
    PROFILE_UPDATED("Profile successfully updated"),
    CATEGORY_ADDED("You've added new category: %s"),
    ERROR_WHILE_ADDING_CATEGORY("Error while adding category %s");

    private final String val;

}