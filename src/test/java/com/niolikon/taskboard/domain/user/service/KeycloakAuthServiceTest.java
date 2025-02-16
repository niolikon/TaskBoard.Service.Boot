package com.niolikon.taskboard.domain.user.service;

import com.niolikon.taskboard.application.exception.rest.BadGatewayRestException;
import com.niolikon.taskboard.application.security.keycloak.KeycloakConfig;
import com.niolikon.taskboard.application.security.keycloak.KeycloakRestClient;
import com.niolikon.taskboard.application.security.keycloak.KeycloakTokenResponse;
import com.niolikon.taskboard.domain.user.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KeycloakAuthServiceTest {
    private static KeycloakConfig KEYCLOAD_CONFIG = KeycloakConfig.builder()
            .clientId("test-client-id")
            .clientSecret("test-client-secret")
            .authServerUrl("http://keycloak/auth")
            .logoutServerUrl("http://keycloak/logout")
            .build();

    private KeycloakRestClient keycloakRestClient;
    private KeycloakAuthService keycloakAuthService;

    @BeforeEach
    void setUp() {
        keycloakRestClient = mock(KeycloakRestClient.class);
        keycloakAuthService = new KeycloakAuthService(KEYCLOAD_CONFIG, keycloakRestClient);
    }

    @Test
    void givenValidCredentials_whenLogin_thenReturnsUserTokenView() {
        UserLoginRequest loginRequest = new UserLoginRequest("user", "password");
        KeycloakTokenResponse keycloakTokenResponse = new KeycloakTokenResponse();
        keycloakTokenResponse.setAccessToken("access-token");
        keycloakTokenResponse.setRefreshToken("refresh-token");
        ResponseEntity<KeycloakTokenResponse> keycloakResponse = new ResponseEntity<>(keycloakTokenResponse, HttpStatus.OK);
        when(keycloakRestClient.postToKeycloak(anyString(), any(HttpEntity.class), eq(KeycloakTokenResponse.class)))
                .thenReturn(keycloakResponse);

        UserTokenView result = keycloakAuthService.login(loginRequest);

        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("access-token");
        assertThat(result.getRefreshToken()).isEqualTo("refresh-token");
        verify(keycloakRestClient, times(1)).postToKeycloak(anyString(), any(HttpEntity.class), eq(KeycloakTokenResponse.class));
    }

    @Test
    void givenInvalidCredentials_whenLogin_thenThrowsException() {
        UserLoginRequest loginRequest = new UserLoginRequest("user", "wrong-password");
        ResponseEntity<KeycloakTokenResponse> errorResponse = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        when(keycloakRestClient.postToKeycloak(anyString(), any(HttpEntity.class), eq(KeycloakTokenResponse.class)))
                .thenReturn(errorResponse);

        assertThrows(BadGatewayRestException.class, () -> keycloakAuthService.login(loginRequest));
        verify(keycloakRestClient, times(1)).postToKeycloak(anyString(), any(HttpEntity.class), eq(KeycloakTokenResponse.class));
    }

    @Test
    void givenValidRefreshToken_whenRefreshToken_thenReturnsNewTokens() {
        UserTokenRefreshRequest refreshRequest = new UserTokenRefreshRequest("old-refresh-token");
        KeycloakTokenResponse keycloakTokenResponse = new KeycloakTokenResponse();
        keycloakTokenResponse.setAccessToken("new-access-token");
        keycloakTokenResponse.setRefreshToken("new-refresh-token");
        ResponseEntity<KeycloakTokenResponse> keycloakResponse = new ResponseEntity<>(keycloakTokenResponse, HttpStatus.OK);
        when(keycloakRestClient.postToKeycloak(anyString(), any(HttpEntity.class), eq(KeycloakTokenResponse.class)))
                .thenReturn(keycloakResponse);

        UserTokenView result = keycloakAuthService.refreshToken(refreshRequest);

        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("new-access-token");
        assertThat(result.getRefreshToken()).isEqualTo("new-refresh-token");
        verify(keycloakRestClient, times(1)).postToKeycloak(anyString(), any(HttpEntity.class), eq(KeycloakTokenResponse.class));
    }

    @Test
    void givenInvalidRefreshToken_whenRefreshToken_thenThrowsException() {
        UserTokenRefreshRequest refreshRequest = new UserTokenRefreshRequest("invalid-refresh-token");
        ResponseEntity<KeycloakTokenResponse> errorResponse = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        when(keycloakRestClient.postToKeycloak(anyString(), any(HttpEntity.class), eq(KeycloakTokenResponse.class)))
                .thenReturn(errorResponse);

        assertThrows(BadGatewayRestException.class, () -> keycloakAuthService.refreshToken(refreshRequest));
        verify(keycloakRestClient, times(1)).postToKeycloak(anyString(), any(HttpEntity.class), eq(KeycloakTokenResponse.class));
    }

    @Test
    void givenValidLogoutRequest_whenLogout_thenCallsKeycloakLogout() {
        UserLogoutRequest logoutRequest = new UserLogoutRequest("valid-refresh-token");
        ResponseEntity<Void> keycloakResponse = new ResponseEntity<>(HttpStatus.OK);
        when(keycloakRestClient.postToKeycloak(anyString(), any(HttpEntity.class), eq(Void.class)))
                .thenReturn(keycloakResponse);

        keycloakAuthService.logout(logoutRequest);

        verify(keycloakRestClient, times(1)).postToKeycloak(anyString(), any(HttpEntity.class), eq(Void.class));
    }

    @Test
    void givenInvalidLogoutRequest_whenLogout_thenThrowsException() {
        UserLogoutRequest logoutRequest = new UserLogoutRequest("invalid-refresh-token");
        ResponseEntity<Void> errorResponse = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        when(keycloakRestClient.postToKeycloak(anyString(), any(HttpEntity.class), eq(Void.class)))
                .thenReturn(errorResponse);

        assertThrows(BadGatewayRestException.class, () -> keycloakAuthService.logout(logoutRequest));

        verify(keycloakRestClient, times(1)).postToKeycloak(anyString(), any(HttpEntity.class), eq(Void.class));
    }
}
