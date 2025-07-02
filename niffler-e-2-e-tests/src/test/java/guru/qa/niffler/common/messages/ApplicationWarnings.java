package guru.qa.niffler.common.messages;

public class ApplicationWarnings {

    public class SignupWarnings {
        public static final String USER_EXISTS = "Username `%s` already exists";
        public static final String INVALID_USERNAME = "Allowed username length should be from 3 to 50 characters";
        public static final String INVALID_PASSWORD = "Allowed password length should be from 3 to 12 characters";
        public static final String PASSWORDS_DO_NOT_MATCH = "Passwords should be equal";
        public static final String VALIDATION_MESSAGE = "Please fill out this field.";
    }

    public class LoginWarnings {
        public static final String BAD_CREDENTIALS = "Bad credentials";
    }

    public class SpendPageAlertMessages {
        public static final String SPEND_CREATED = "New spending is successfully created";
        public static final String SPEND_DELETED = "Spendings succesfully deleted";
        public static final String SPEND_UPDATED = "Spending is edited successfully";
    }

    public class ProfileAlertMessages {
        public static final String PROFILE_UPDATED = "Profile successfully updated";
        public static final String CATEGORY_ADDED = "You've added new category: %s";
        public static final String ERROR_WHILE_ADDING_CATEGORY = "Error while adding category %s";
    }
}
