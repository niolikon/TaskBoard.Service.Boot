package com.niolikon.taskboard.application.security.keycloak;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Objects;

public class KeycloakRequestEntityBuilder {
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

        if (Objects.nonNull(clientId) && (!clientId.isEmpty())) {
            requestBody.add("client_id", clientId);
        }
        if (Objects.nonNull(clientSecret) && (!clientSecret.isEmpty())) {
            requestBody.add("client_secret", clientSecret);
        }
        if (Objects.nonNull(username) && (!username.isEmpty())) {
            requestBody.add("username", username);
        }
        if (Objects.nonNull(password) && (!password.isEmpty())) {
            requestBody.add("password", password);
        }
        if (Objects.nonNull(refreshToken) && (!refreshToken.isEmpty())) {
            requestBody.add("refresh_token", refreshToken);
        }
        if (Objects.nonNull(grantType) && (!grantType.isEmpty())) {
            requestBody.add("grant_type", grantType);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        return new HttpEntity<>(requestBody, headers);
    }
}
