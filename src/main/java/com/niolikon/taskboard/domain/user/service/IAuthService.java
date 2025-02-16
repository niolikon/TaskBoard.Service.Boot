package com.niolikon.taskboard.domain.user.service;

import com.niolikon.taskboard.domain.user.dto.UserLoginRequest;
import com.niolikon.taskboard.domain.user.dto.UserLogoutRequest;
import com.niolikon.taskboard.domain.user.dto.UserTokenRefreshRequest;
import com.niolikon.taskboard.domain.user.dto.UserTokenView;

public interface IAuthService {
    UserTokenView login(UserLoginRequest userLoginRequest);

    UserTokenView refreshToken(UserTokenRefreshRequest userTokenRefreshRequest);

    void logout(UserLogoutRequest userLogoutRequest);
}
