package com.niolikon.taskboard.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserTokenRefreshRequest {
    @NotEmpty
    @Size(min = 10)
    @JsonProperty("RefreshToken")
    private String refreshToken;
}
