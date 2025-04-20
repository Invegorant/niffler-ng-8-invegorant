package guru.qa.niffler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.data.entity.userdata.UserEntity;

import java.util.Arrays;
import java.util.UUID;

public record UserJson(
        @JsonProperty("id")
        UUID id,

        @JsonProperty("username")
        String username,

        @JsonProperty("firstname")
        String firstname,

        @JsonProperty("surname")
        String surname,

        @JsonProperty("fullname")
        String fullname,

        @JsonProperty("currency")
        CurrencyValues currency,

        @JsonProperty("photo")
        String photo,

        @JsonProperty("photoSmall")
        String photoSmall) {
    public static UserJson fromEntity(UserEntity entity) {
        return new UserJson(
                entity.getId(),
                entity.getUsername(),
                entity.getFirstname(),
                entity.getSurname(),
                entity.getFullname(),
                entity.getCurrency(),
                Arrays.toString(entity.getPhoto()),
                Arrays.toString(entity.getPhotoSmall())
        );
    }
}