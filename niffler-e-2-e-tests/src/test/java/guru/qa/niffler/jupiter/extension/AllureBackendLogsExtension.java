package guru.qa.niffler.jupiter.extension;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.TestResult;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class AllureBackendLogsExtension implements SuiteExtension {

    public static final String caseName = "Niffler backend logs";

    @SneakyThrows
    @Override
    public void afterSuite() {
        final AllureLifecycle allureLifecycle = Allure.getLifecycle();
        final String caseId = UUID.randomUUID().toString();
        allureLifecycle.scheduleTestCase(new TestResult().setUuid(caseId).setName(caseName));
        allureLifecycle.startTestCase(caseId);

        addNifflerAttachment(allureLifecycle, "niffler-auth");
        addNifflerAttachment(allureLifecycle, "niffler-currency");
        addNifflerAttachment(allureLifecycle, "niffler-gateway");
        addNifflerAttachment(allureLifecycle, "niffler-spend");
        addNifflerAttachment(allureLifecycle, "niffler-userdata");

        allureLifecycle.stopTestCase(caseId);
        allureLifecycle.writeTestCase(caseId);

    }

    @SneakyThrows
    private void addNifflerAttachment(AllureLifecycle allureLifecycle, String nifflerName) {
        allureLifecycle.addAttachment(
                nifflerName + " log",
                "text/html",
                ".log",
                Files.newInputStream(
                        Path.of("./logs/" + nifflerName + "/app.log")
                )
        );
    }
}
