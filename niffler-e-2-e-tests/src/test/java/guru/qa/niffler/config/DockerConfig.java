package guru.qa.niffler.config;

import javax.annotation.Nonnull;

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
}
