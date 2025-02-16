package com.niolikon.taskboard.domain.user.service;

import com.niolikon.taskboard.application.exception.rest.BadGatewayRestException;
import com.niolikon.taskboard.application.security.keycloak.KeycloakConfig;
import com.niolikon.taskboard.application.security.keycloak.KeycloakRequestEntityBuilder;
import com.niolikon.taskboard.application.security.keycloak.KeycloakRestClient;
import com.niolikon.taskboard.application.security.keycloak.KeycloakTokenResponse;
import com.niolikon.taskboard.domain.user.dto.*;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.http.*;

@Service
@Log
public class KeycloakAuthService implements IAuthService {

    private final KeycloakConfig keycloakConfig;
    private final KeycloakRestClient keycloakRestClient;

    public KeycloakAuthService(KeycloakConfig config, KeycloakRestClient requestHandler) {
        this.keycloakConfig = config;
        this.keycloakRestClient = requestHandler;
    }

    @Override
    public UserTokenView login(UserLoginRequest userLoginRequest) {
        HttpEntity<MultiValueMap<String, String>> requestEntity = KeycloakRequestEntityBuilder.builder()
                .withClientId(keycloakConfig.getClientId())
                .withUsername(userLoginRequest.getUsername())
                .withPassword(userLoginRequest.getPassword())
                .withPasswordGrantType()
                .build();
        log.finer(String.format("Request formed: %s", requestEntity));

        ResponseEntity<KeycloakTokenResponse> responseEntity = keycloakRestClient.postToKeycloak(
                keycloakConfig.getAuthServerUrl(),
                requestEntity,
                KeycloakTokenResponse.class
        );
        log.finer(String.format("Response received: %s", responseEntity));

        if (! responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new BadGatewayRestException("Authentication request failed");
        }
        UserTokenView userTokenView = new UserTokenView();
        userTokenView.setAccessToken(responseEntity.getBody().getAccessToken());
        userTokenView.setRefreshToken(responseEntity.getBody().getRefreshToken());

        return userTokenView;
    }

    @Override
    public UserTokenView refreshToken(UserTokenRefreshRequest userTokenRefreshRequest) {
        HttpEntity<MultiValueMap<String, String>> requestEntity = KeycloakRequestEntityBuilder.builder()
                .withClientId(keycloakConfig.getClientId())
                .withRefreshToken(userTokenRefreshRequest.getRefreshToken())
                .withRefreshTokenGrantType()
                .build();
        log.finer(String.format("Request formed: %s", requestEntity));

        ResponseEntity<KeycloakTokenResponse> responseEntity = keycloakRestClient.postToKeycloak(
                keycloakConfig.getAuthServerUrl(),
                requestEntity,
                KeycloakTokenResponse.class
        );
        log.finer(String.format("Response received: %s", responseEntity));

        if (! responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new BadGatewayRestException("Authentication request failed");
        }
        UserTokenView userTokenView = new UserTokenView();
        userTokenView.setAccessToken(responseEntity.getBody().getAccessToken());
        userTokenView.setRefreshToken(responseEntity.getBody().getRefreshToken());

        return userTokenView;
    }

    @Override
    public void logout(UserLogoutRequest userLogoutRequest) {
        HttpEntity<MultiValueMap<String, String>> requestEntity = KeycloakRequestEntityBuilder.builder()
                .withClientId(keycloakConfig.getClientId())
                .withRefreshToken(userLogoutRequest.getRefreshToken())
                .withRefreshTokenGrantType()
                .build();
        log.finer(String.format("Request formed: %s", requestEntity));

        ResponseEntity<Void> responseEntity = keycloakRestClient.postToKeycloak(
                keycloakConfig.getLogoutServerUrl(),
                requestEntity,
                Void.class
        );
        log.finer(String.format("Response received: %s", responseEntity));

        if (! responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new BadGatewayRestException("Logout request failed");
        }
    }
}
