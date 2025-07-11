package guru.qa.niffler.api;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.DataFilterValues;
import guru.qa.niffler.model.pageable.RestResponsePage;
import guru.qa.niffler.model.rest.UserJson;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

import javax.annotation.Nullable;

public interface GatewayV2Api {

    @GET("api/v2/friends/all")
    Call<RestResponsePage<UserJson>> allFriends(@Header("Authorization") String bearerToken,
                                                @Query("page") int page,
                                                @Query("size") int size,
                                                @Query("searchQuery") @Nullable String searchQuery);

    @GET("api/v2/users/all")
    Call<RestResponsePage<UserJson>> allUsers(@Header("Authorization") String bearerToken,
                                              @Query("page") int page,
                                              @Query("size") int size,
                                              @Query("sort") String sort,
                                              @Nullable @Query("searchQuery") String searchQuery);

    @GET("api/v2/spends/all")
    Call<RestResponsePage<UserJson>> allSpends(@Header("Authorization") String bearerToken,
                                               @Query("page") int page,
                                               @Query("filterCurrency") String filterCurrency,
                                               @Query("filterPeriod") DataFilterValues filterPeriod,
                                               @Nullable @Query("searchQuery") String searchQuery);

    @GET("api/v2/stat/total")
    Call<RestResponsePage<JsonNode>> totalStat(@Header("Authorization") String bearerToken,
                                               @Query("statCurrency") CurrencyValues statCurrency,
                                               @Nullable @Query("filterCurrency") CurrencyValues filterCurrency,
                                               @Nullable @Query("filterPeriod") DataFilterValues filterPeriod);

}