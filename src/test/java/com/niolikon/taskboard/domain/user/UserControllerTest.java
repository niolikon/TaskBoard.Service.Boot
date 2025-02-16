package com.niolikon.taskboard.domain.user;

import com.niolikon.taskboard.domain.user.dto.UserLoginRequest;
import com.niolikon.taskboard.domain.user.dto.UserLogoutRequest;
import com.niolikon.taskboard.domain.user.dto.UserTokenRefreshRequest;
import com.niolikon.taskboard.domain.user.dto.UserTokenView;
import com.niolikon.taskboard.domain.user.service.IAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    private static final String FAKE_ACCESS_TOKEN_STRING = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJueHBkNGFQS1Rqbl8yNzlGbVVJTW5HaEtaX3RHR25QSUFFcURQS1B2dk13In0.eyJleHAiOjE3Mzk4NzE5OTAsImlhdCI6MTczOTg3MTY5MCwianRpIjoiOGUwM2U0YTgtNjMyNS00MzRjLWE4MzMtNzg4OGM3YzRlMDkxIiwiaXNzIjoiaHR0cDovL3Rhc2tib2FyZC1rZXljbG9hazo4MDgwL3JlYWxtcy9Ub2RvUmVhbG0iLCJzdWIiOiJkZjRmNmFlMC0xNzVhLTQ0MTgtYTU1My01M2FkNWVhNjU3YzAiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJteS1jbGllbnQiLCJzZXNzaW9uX3N0YXRlIjoiMWVlNGJlZmMtZGJkNC00MzAyLWFiNDUtODVkZjlmYWVhMTcxIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIqIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJVU0VSIl19LCJzY29wZSI6InByb2ZpbGUgZW1haWwiLCJzaWQiOiIxZWU0YmVmYy1kYmQ0LTQzMDItYWI0NS04NWRmOWZhZWExNzEiLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwibmFtZSI6IlNpbXBsZSBVc2VyIiwicHJlZmVycmVkX3VzZXJuYW1lIjoidGVzdHVzZXIiLCJnaXZlbl9uYW1lIjoiU2ltcGxlIiwiZmFtaWx5X25hbWUiOiJVc2VyIiwiZW1haWwiOiJ1c2VyQGV4YW1wbGUuY29tIn0.KRSugp-U2Gqj01Bj6ws7XhNvov6nUC0DAihvpU8bnRSiRy9L9GP0alHC4Lzew2z0Hf7YawyhCF9XAZnIpsAFEUzSkrv4xiwUYfDsIsLWWURVg3EWlkRXkWHxsRRqy7lfhqN_UTc5_pIkHHG9Ao6Xvp81_Y4-AEQAyrr7rn5vAD5igDWwVr6-tfsmBVCJR0zaJ-Nn5xvntjPsMJZRbuheryEwlfUEIpe6iVWdZ3k8G5z5ZrO5meyz1-_djb5PtEjFwfFYlBz125TOQmYevUxxKQtbFgMSSHxzBzsTplYD186jglBcPMy6srQFmCQ955P7pGWDF4hN1dtb0A7yfV1bmw";
    private static final String FAKE_REFRESH_TOKEN_STRING = "eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICIyYWRlMWZhZC1jYjc0LTQzYjgtOTllNi0zMDEwMjE4ZDAyZjMifQ.eyJleHAiOjE3Mzk4NzM0OTAsImlhdCI6MTczOTg3MTY5MCwianRpIjoiZTNhNzFkNTYtM2YzZS00OGZkLWIyZDYtMWYxYjRjMGFiMDQ0IiwiaXNzIjoiaHR0cDovL3Rhc2tib2FyZC1rZXljbG9hazo4MDgwL3JlYWxtcy9Ub2RvUmVhbG0iLCJhdWQiOiJodHRwOi8vdGFza2JvYXJkLWtleWNsb2FrOjgwODAvcmVhbG1zL1RvZG9SZWFsbSIsInN1YiI6ImRmNGY2YWUwLTE3NWEtNDQxOC1hNTUzLTUzYWQ1ZWE2NTdjMCIsInR5cCI6IlJlZnJlc2giLCJhenAiOiJteS1jbGllbnQiLCJzZXNzaW9uX3N0YXRlIjoiMWVlNGJlZmMtZGJkNC00MzAyLWFiNDUtODVkZjlmYWVhMTcxIiwic2NvcGUiOiJwcm9maWxlIGVtYWlsIiwic2lkIjoiMWVlNGJlZmMtZGJkNC00MzAyLWFiNDUtODVkZjlmYWVhMTcxIn0.HjN3SUKPJCgb7wUWQyP2WzEhSFtAK3EOz0Q82gLmvo4";

    private IAuthService authService;
    private UserController userController;

    @BeforeEach
    void setUp() {
        authService = mock(IAuthService.class);
        userController = new UserController(authService);
    }

    @Test
    void givenValidLoginRequest_whenLogin_thenReturnsUserTokenView() {
        UserLoginRequest request = new UserLoginRequest("testuser", "test57r0ngP455!");
        UserTokenView expectedToken = new UserTokenView(FAKE_ACCESS_TOKEN_STRING, FAKE_REFRESH_TOKEN_STRING);
        when(authService.login(request)).thenReturn(expectedToken);

        ResponseEntity<UserTokenView> response = userController.login(request);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo(expectedToken);
        assertThat(response.getBody().getAccessToken()).isEqualTo(FAKE_ACCESS_TOKEN_STRING);
        assertThat(response.getBody().getRefreshToken()).isEqualTo(FAKE_REFRESH_TOKEN_STRING);
        verify(authService, times(1)).login(request);
    }

    @Test
    void givenValidRefreshRequest_whenRefreshToken_thenReturnsUserTokenView() {
        UserTokenRefreshRequest request = new UserTokenRefreshRequest("valid-refresh-token");
        UserTokenView expectedToken = new UserTokenView(FAKE_ACCESS_TOKEN_STRING, FAKE_REFRESH_TOKEN_STRING);
        when(authService.refreshToken(request)).thenReturn(expectedToken);

        ResponseEntity<UserTokenView> response = userController.refreshToken(request);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo(expectedToken);
        assertThat(response.getBody().getAccessToken()).isEqualTo(FAKE_ACCESS_TOKEN_STRING);
        assertThat(response.getBody().getRefreshToken()).isEqualTo(FAKE_REFRESH_TOKEN_STRING);
        verify(authService, times(1)).refreshToken(request);
    }

    @Test
    void givenValidLogoutRequest_whenLogout_thenReturnsOk() {
        UserLogoutRequest request = new UserLogoutRequest("valid-refresh-token");

        ResponseEntity<Void> response = userController.logout(request);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        verify(authService, times(1)).logout(request);
    }
}
