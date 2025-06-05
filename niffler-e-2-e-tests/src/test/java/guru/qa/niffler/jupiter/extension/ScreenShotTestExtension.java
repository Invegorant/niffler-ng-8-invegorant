package guru.qa.niffler.jupiter.extension;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.model.allure.ScreenDif;
import io.qameta.allure.Allure;
import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class ScreenShotTestExtension implements ParameterResolver, TestExecutionExceptionHandler {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ScreenShotTestExtension.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Base64.Encoder encoder = Base64.getEncoder();

    public static BufferedImage getExpected() {
        return TestsMethodContextExtension.context().getStore(NAMESPACE).get("expected", BufferedImage.class);
    }

    public static void setExpected(BufferedImage expected) {
        TestsMethodContextExtension.context().getStore(NAMESPACE).put("expected", expected);
    }

    public static BufferedImage getActual() {
        return TestsMethodContextExtension.context().getStore(NAMESPACE).get("actual", BufferedImage.class);
    }

    public static void setActual(BufferedImage actual) {
        TestsMethodContextExtension.context().getStore(NAMESPACE).put("actual", actual);
    }

    public static BufferedImage getDiff() {
        return TestsMethodContextExtension.context().getStore(NAMESPACE).get("diff", BufferedImage.class);
    }

    public static void setDiff(BufferedImage diff) {
        TestsMethodContextExtension.context().getStore(NAMESPACE).put("diff", diff);
    }

    private static byte[] imageToBytes(BufferedImage image) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return AnnotationSupport.isAnnotated(extensionContext.getRequiredTestMethod(), ScreenShotTest.class) &&
                parameterContext.getParameter().getType().isAssignableFrom(BufferedImage.class);
    }

    @SneakyThrows
    @Override
    public BufferedImage resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return ImageIO.read(new ClassPathResource("img/expected-stat.png").getInputStream());
    }

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        ScreenDif screenDif = new ScreenDif(
                "data:image/png;base64," + encoder.encodeToString(imageToBytes(getExpected())),
                "data:image/png;base64," + encoder.encodeToString(imageToBytes(getActual())),
                "data:image/png;base64," + encoder.encodeToString(imageToBytes(getDiff()))
        );

        Allure.addAttachment(
                "Screenshot diff",
                "application/vnd.allure.image.diff",
                objectMapper.writeValueAsString(screenDif)
        );
        throw throwable;
    }
}