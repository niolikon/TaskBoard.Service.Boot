package com.niolikon.taskboard.application.security.keycloak;

import org.mapstruct.ap.internal.util.Strings;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Objects;

public class KeycloakRequestEntityBuilder extends HttpEntity<MultiValueMap<String, String>> {
    private String clientId;
    private String clientSecret;
    private String username;
    private String password;
    private String refreshToken;
    private String grantType;

    private KeycloakRequestEntityBuilder() {
        clientId = null;
        clientSecret = null;
        username = null;
        password = null;
        refreshToken = null;
        grantType = null;
    }

    public static KeycloakRequestEntityBuilder builder() {
        return new KeycloakRequestEntityBuilder();
    }

    public KeycloakRequestEntityBuilder withClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public KeycloakRequestEntityBuilder withClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }

    public KeycloakRequestEntityBuilder withUsername(String username) {
        this.username = username;
        return this;
    }

    public KeycloakRequestEntityBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public KeycloakRequestEntityBuilder withRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }

    public KeycloakRequestEntityBuilder withPasswordGrantType() {
        this.grantType = "password";
        return this;
    }

    public KeycloakRequestEntityBuilder withRefreshTokenGrantType() {
        this.grantType = "refresh_token";
        return this;
    }

    public HttpEntity<MultiValueMap<String, String>> build() {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();

        if (Objects.nonNull(clientId) && Strings.isNotEmpty(clientId)) {
            requestBody.add("client_id", clientId);
        }
        if (Objects.nonNull(clientSecret) && Strings.isNotEmpty(clientSecret)) {
            requestBody.add("client_secret", clientSecret);
        }
        if (Objects.nonNull(username) && Strings.isNotEmpty(username)) {
            requestBody.add("username", username);
        }
        if (Objects.nonNull(password) && Strings.isNotEmpty(password)) {
            requestBody.add("password", password);
        }
        if (Objects.nonNull(refreshToken) && Strings.isNotEmpty(refreshToken)) {
            requestBody.add("refresh_token", refreshToken);
        }
        if (Objects.nonNull(grantType) && Strings.isNotEmpty(grantType)) {
            requestBody.add("grant_type", grantType);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        return new HttpEntity<>(requestBody, headers);
    }
}
