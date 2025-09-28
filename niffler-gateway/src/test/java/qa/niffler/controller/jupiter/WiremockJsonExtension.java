package qa.niffler.controller.jupiter;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

public class WiremockJsonExtension implements BeforeEachCallback, AfterEachCallback {

    private final ThreadLocal<WireMockServer> threadLocal = new ThreadLocal<>();

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        AnnotationSupport.findAnnotation(extensionContext.getRequiredTestMethod(), WiremockStubs.class)
                .ifPresent(wmAnno -> {
                    WireMockServer wiremock = new WireMockServer(
                            new WireMockConfiguration()
                                    .port(wmAnno.port())
                    );
                    wiremock.start();
                    threadLocal.set(wiremock);
                });

    }

    @Override
    public void afterEach(ExtensionContext extensionContext) throws Exception {
        if (threadLocal.get() != null && threadLocal.get().isRunning()) {
            threadLocal.get().shutdown();
            threadLocal.remove();
        }
    }
}