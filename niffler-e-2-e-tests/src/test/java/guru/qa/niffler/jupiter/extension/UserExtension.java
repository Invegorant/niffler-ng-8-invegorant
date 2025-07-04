package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.service.impl.UsersApiClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static guru.qa.niffler.test.web.AbstractTest.DEFAULT_PASSWORD;

@ParametersAreNonnullByDefault
public class UserExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserExtension.class);
    private static final String defaultPassword = DEFAULT_PASSWORD;

    private final UsersClient usersClient = new UsersApiClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(userAnno -> {
                    if ("".equals((userAnno.username()))) {
                        final String username = RandomDataUtils.randomUsername();
                        UserJson user = usersClient.createUser(username, defaultPassword);
                        usersClient.createIncomeInvitations(user, userAnno.incomeInvitations());
                        usersClient.createOutcomeInvitations(user, userAnno.outcomeInvitations());
                        usersClient.createFriends(user, userAnno.friends());
                        context.getStore(NAMESPACE).put(
                                context.getUniqueId(),
                                user
                        );
                    }
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(UserJson.class);
    }

    @Override
    public UserJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return createdUser();
    }

    @Nullable
    public static UserJson createdUser() {
        final ExtensionContext context = TestsMethodContextExtension.context();
        return context.getStore(NAMESPACE).get(context.getUniqueId(), UserJson.class);
    }
}
