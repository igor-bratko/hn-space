package com.us.spaces.hn.auth;

import com.us.framework.model.server.HttpRequest;
import com.us.framework.model.server.HttpResponse;
import com.us.spaces.hn.ErrorDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpCookie;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    public static final String SET_COOKIE = "Set-Cookie";

    private final UserRepository userRepository;
    private final SessionService sessionService;
    private final UserService userService;

    public AuthController(UserRepository userRepository,
                          SessionService sessionService,
                          UserService userService) {
        this.userRepository = userRepository;
        this.sessionService = sessionService;
        this.userService = userService;
    }

    public HttpResponse signUp(HttpRequest req) {
        try {
            var userReq = req.body().bodyAs(SignUpRequest.class);
            if (userReq.username() == null || userReq.password() == null) {
                return new HttpResponse()
                        .body(new ErrorDto("Login error", "Request contains empty values"))
                        .status(400);
            }

            var hashedPass = PasswordHasher.hashPassword(userReq.password());
            var id = userRepository.createUser(userReq.username(), hashedPass);

            return new HttpResponse().body(id).status(200);
        } catch (Exception e) {
            log.error("Failed to create user", e);

            var error = new ErrorDto("Failed to create user", e.getMessage());

            return new HttpResponse().body(error).status(500);
        }
    }

    public HttpResponse signIn(HttpRequest req) {
        var userReq = req.body().bodyAs(SignInRequest.class);
        if (userReq.username() == null || userReq.password() == null) {
            return new HttpResponse()
                    .body(new ErrorDto("Login error", "Request contains empty values"))
                    .status(400);
        }

        var user = userService.login(userReq.username(), userReq.password());

        var session = sessionService.createSession(userReq.username(), userReq.password());

        var sessionCookie = new HttpCookie("u_session_id", session);
        sessionCookie.setHttpOnly(true);
        sessionCookie.setSecure(true);
        sessionCookie.setPath("/");
        sessionCookie.setMaxAge(7 * 24 * 60 * 60);

        var userIdCookie = new HttpCookie("u_user_id", user.id());
        userIdCookie.setHttpOnly(true);
        userIdCookie.setSecure(true);
        userIdCookie.setPath("/");
        userIdCookie.setMaxAge(7 * 24 * 60 * 60);

        return new HttpResponse()
                .status(200)
                .addHeader(SET_COOKIE, sessionCookie.toString())
                .addHeader(SET_COOKIE, userIdCookie.toString());
    }

    public HttpResponse logout(HttpRequest req) {
        var sessionCookie = req.getCookies().get("u_session_id");
        requireNonNull(sessionCookie);

        sessionService.deleteSession(sessionCookie.getValue());

        var removeSessionCookie = new HttpCookie("u_session_id", "");
        sessionCookie.setHttpOnly(true);
        sessionCookie.setSecure(true);
        sessionCookie.setPath("/");
        sessionCookie.setMaxAge(0);

        var removeUserIdCookie = new HttpCookie("u_user_id", "");
        sessionCookie.setHttpOnly(true);
        sessionCookie.setSecure(true);
        sessionCookie.setPath("/");
        sessionCookie.setMaxAge(0);

        return new HttpResponse()
                .status(200)
                .addHeader(SET_COOKIE, removeSessionCookie.toString())
                .addHeader(SET_COOKIE, removeUserIdCookie.toString());
    }

    public HttpResponse me(HttpRequest req) {
        var sessionCookie = req.getCookies().get("u_session_id");
        if (sessionCookie == null) {
            log.info("u_session_id cookies is absent");

            return new HttpResponse().status(401).body(Map.of("error", "401"));
        }

        try {
            var id = sessionService.get(sessionCookie.getValue()).userId();
            return new HttpResponse()
                    .status(200)
                    .body(Map.of("userId", id));
        } catch (Exception e) {
            log.error("Error: ", e);

            return new HttpResponse().status(401).body(Map.of("error", "401"));
        }
    }

    public record SignUpRequest(String username, String password) {
    }

    public record SignInRequest(String username, String password) {
    }
}
