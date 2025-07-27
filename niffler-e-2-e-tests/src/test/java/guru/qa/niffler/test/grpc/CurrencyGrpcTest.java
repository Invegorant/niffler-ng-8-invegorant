package guru.qa.niffler.test.grpc;

import com.google.protobuf.Empty;
import guru.qa.niffler.grpc.*;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Feature("GRPC -> Currency")
public class CurrencyGrpcTest extends BaseGrpcTest {

    static Stream<Arguments> calculatedRateDataProvider() {
        return Stream.of(Arguments.arguments(CurrencyValues.USD, 1000.00, 15.0),
                Arguments.arguments(CurrencyValues.KZT, 2000.00, 14285.71),
                Arguments.arguments(CurrencyValues.EUR, 500, 6.94)
        );
    }

    static Stream<Arguments> calculatedRateForMinSpendDataProvider() {
        return Stream.of(
                Arguments.arguments(CurrencyValues.USD, 0.0),
                Arguments.arguments(CurrencyValues.KZT, 0.07),
                Arguments.arguments(CurrencyValues.EUR, 0.0)
        );
    }

    @Test
    @DisplayName("GRPC -> Currency - проверяем getAllCurrencies")
    void grpc_allCurrenciesShouldReturned() {
        final CurrencyResponse response = BLOCKING_STUB.getAllCurrencies(Empty.getDefaultInstance());
        final List<Currency> allCurrenciesList = response.getAllCurrenciesList();

        assertEquals(4, allCurrenciesList.size());
    }

    @ParameterizedTest
    @MethodSource("calculatedRateDataProvider")
    @DisplayName("GRPC -> Currency - проверяем calculateRate")
    void grpc_shouldCalculateAmountForSpendsInRUB(CurrencyValues targetCurrency, double amount, double expectedValue) {
        final CalculateRequest request = CalculateRequest.newBuilder()
                .setAmount(amount)
                .setSpendCurrency(CurrencyValues.RUB)
                .setDesiredCurrency(targetCurrency)
                .build();

        final CalculateResponse calculateResponse = BLOCKING_STUB.calculateRate(request);
        assertEquals(expectedValue, calculateResponse.getCalculatedAmount());
    }

    @ParameterizedTest
    @MethodSource("calculatedRateForMinSpendDataProvider")
    @DisplayName("GRPC -> Currency - проверяем calculateRate for min spends")
    void grpc_shouldCalculateAmountForMinSpendsInRUB(CurrencyValues targetCurrency, double expectedValue) {
        final double amount = 0.01;
        final CalculateRequest request = CalculateRequest.newBuilder()
                .setAmount(amount)
                .setSpendCurrency(CurrencyValues.RUB)
                .setDesiredCurrency(targetCurrency)
                .build();

        final CalculateResponse calculateResponse = BLOCKING_STUB.calculateRate(request);
        assertEquals(expectedValue, calculateResponse.getCalculatedAmount());
    }

}
