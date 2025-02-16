package com.niolikon.taskboard.domain.user;

import com.niolikon.taskboard.domain.user.dto.UserLoginRequest;
import com.niolikon.taskboard.domain.user.dto.UserLogoutRequest;
import com.niolikon.taskboard.domain.user.dto.UserTokenRefreshRequest;
import com.niolikon.taskboard.domain.user.dto.UserTokenView;
import com.niolikon.taskboard.domain.user.service.IAuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping("/api/Users")
public class UserController {

    private final IAuthService authService;

    public UserController(IAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("login")
    public ResponseEntity<UserTokenView> login(@Valid @RequestBody UserLoginRequest userLoginRequest) {
        UserTokenView userTokenView = authService.login(userLoginRequest);
        return ok().body(userTokenView);
    }

    @PostMapping("refresh")
    public ResponseEntity<UserTokenView> refreshToken(@Valid @RequestBody UserTokenRefreshRequest userTokenRefreshRequest) {
        UserTokenView userTokenView = authService.refreshToken(userTokenRefreshRequest);
        return ok().body(userTokenView);
    }

    @PostMapping("logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody UserLogoutRequest userLogoutRequest) {
        authService.logout(userLogoutRequest);
        return ok().build();
    }
}
