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
public class UserLoginRequest {
    @NotEmpty
    @Size(min = 4, max = 50)
    @JsonProperty("UserName")
    private String username;

    @NotEmpty
    @Size(min = 4, max = 50)
    @JsonProperty("PassWord")
    private String password;
}
