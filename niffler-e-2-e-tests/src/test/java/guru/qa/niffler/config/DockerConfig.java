package guru.qa.niffler.config;

import javax.annotation.Nonnull;
import java.util.Objects;

public enum DockerConfig implements Config {
    INSTANCE;

    @Nonnull
    @Override
    public String frontUrl() {
        return "";
    }

    @Nonnull
    @Override
    public String authUrl() {
        return "";
    }

    @Nonnull
    @Override
    public String authJdbcUrl() {
        return "";
    }

    @Nonnull
    @Override
    public String gatewayUrl() {
        return "";
    }

    @Nonnull
    @Override
    public String userdataUrl() {
        return "";
    }

    @Nonnull
    @Override
    public String userdataJdbcUrl() {
        return "";
    }

    @Nonnull
    @Override
    public String spendUrl() {
        return "";
    }

    @Nonnull
    @Override
    public String spendJdbcUrl() {
        return "";
    }

    @Nonnull
    @Override
    public String currencyJdbcUrl() {
        return "";
    }

    @Override
    public String allureDockerServiceUrl() {
        String allureDockerApiUrl = System.getenv("ALLURE_DOCKER_API");
        return Objects.requireNonNullElse(allureDockerApiUrl, "http://allure:5050/");
    }

    @Nonnull
    @Override
    public String currencyGrpcAddress() {
        return "";
    }

    @Nonnull
    @Override
    public String userdataGrpcAddress() {
        return "";
    }
}
