package guru.qa.niffler.jupiter.extension;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.model.TestData;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.service.impl.AuthApiClient;
import guru.qa.niffler.service.impl.SpendApiClient;
import guru.qa.niffler.service.impl.UsersApiClient;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.openqa.selenium.Cookie;

import javax.annotation.ParametersAreNonnullByDefault;

import static guru.qa.niffler.jupiter.extension.TestsMethodContextExtension.context;

@ParametersAreNonnullByDefault
public class ApiLoginExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ApiLoginExtension.class);
    private static final Config CFG = Config.getInstance();

    private final boolean setupBrowser;
    private final SpendApiClient spendApiClient = new SpendApiClient();
    private final UsersApiClient usersApiClient = new UsersApiClient();
    private final AuthApiClient authApiClient = new AuthApiClient();

    private ApiLoginExtension(boolean setupBrowser) {
        this.setupBrowser = setupBrowser;
    }

    public ApiLoginExtension() {
        this.setupBrowser = true;
    }

    public static ApiLoginExtension restApiLoginExtension() {
        return new ApiLoginExtension(false);
    }

    public static String getToken() {
        return context().getStore(NAMESPACE).get("token", String.class);
    }

    public static void setToken(String token) {
        context().getStore(NAMESPACE).put("token", token);
    }

    public static String getCode() {
        return context().getStore(NAMESPACE).get("code", String.class);
    }

    public static void setCode(String code) {
        context().getStore(NAMESPACE).put("code", code);
    }

    public static Cookie getJsessionIdCookie() {
        return new Cookie(
                "JSESSIONID",
                ThreadSafeCookieStore.INSTANCE.getCookieValue("JSESSIONID")
        );
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), ApiLogin.class)
                .ifPresent(apiLogin -> {

                    final UserJson userToLogin;
                    final UserJson userFromUserExtension = UserExtension.createdUser();
                    if ("".equals(apiLogin.username()) || "".equals(apiLogin.password())) {
                        if (userFromUserExtension == null) {
                            throw new IllegalStateException("@User must be present in case that @ApiLogin is empty!");
                        }
                        userToLogin = userFromUserExtension;
                    } else {
                        UserJson fakeUser = fillUserInfo(apiLogin.username(), apiLogin.password());
                        if (userFromUserExtension != null) {
                            throw new IllegalStateException("@User must not be present in case that @ApiLogin contains username or password!");
                        }
                        UserExtension.setUser(fakeUser);
                        userToLogin = fakeUser;
                    }

                    final String token = authApiClient.login(
                            userToLogin.username(),
                            userToLogin.testData().password()
                    );
                    setToken(token);
                    if (setupBrowser) {
                        Selenide.open(CFG.frontUrl());
                        Selenide.localStorage().setItem("id_token", getToken());
                        WebDriverRunner.getWebDriver().manage().addCookie(
                                getJsessionIdCookie()
                        );
                        Selenide.open(CFG.frontUrl() + "main");
                        new MainPage().checkMainPageIsOpened();
                    }
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(String.class)
                && AnnotationSupport.isAnnotated(parameterContext.getParameter(), Token.class);
    }

    @Override
    public String resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return getToken();
    }

    private UserJson fillUserInfo(String username, String password) {
        UserJson user = usersApiClient.currentUser(username);
        TestData testData = new TestData(
                password,
                spendApiClient.getCategories(username, false),
                spendApiClient.getSpends(username, null, null, null),
                usersApiClient.getFriends(username),
                usersApiClient.getIncomeInvitations(username),
                usersApiClient.getOutcomeInvitations(username)
        );
        return user.withTestData(testData);
    }
}