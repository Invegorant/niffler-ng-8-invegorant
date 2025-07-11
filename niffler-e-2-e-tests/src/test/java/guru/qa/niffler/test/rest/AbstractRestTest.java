package guru.qa.niffler.test.rest;

import guru.qa.niffler.jupiter.annotation.meta.RestTest;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.service.impl.GatewayApiClient;
import guru.qa.niffler.service.impl.GatewayV2ApiClient;
import guru.qa.niffler.service.impl.UsersApiClient;
import org.junit.jupiter.api.extension.RegisterExtension;

@RestTest
public abstract class AbstractRestTest {

    @RegisterExtension
    static ApiLoginExtension apiLoginExtension = ApiLoginExtension.rest();

    protected final UsersApiClient userApiClient = new UsersApiClient();
    protected final GatewayApiClient gwApiClient = new GatewayApiClient();
    protected final GatewayV2ApiClient gwV2ApiClient = new GatewayV2ApiClient();
}
