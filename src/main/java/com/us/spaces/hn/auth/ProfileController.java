package com.us.spaces.hn.auth;

import com.us.framework.model.server.HttpRequest;
import com.us.framework.model.server.HttpResponse;
import com.us.spaces.hn.ErrorDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ProfileController {
    private static final Logger log = LoggerFactory.getLogger(ProfileController.class);

    private final UserRepository userRepository;

    public ProfileController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public HttpResponse getProfile(HttpRequest req) {
        try {
            var userIdCookie = req.getCookies().get("u_user_id");
            if (userIdCookie == null) {
                log.info("u_user_id cookie is absent");

                return new HttpResponse().status(401).body(Map.of("error", "401"));
            }

            var userProfile = userRepository.get(userIdCookie.getValue());

            return new HttpResponse().body(userProfile).status(200);
        } catch (Exception e) {
            log.error("Error", e);
            return new HttpResponse().status(500).body(new ErrorDto("Server error", e.getMessage()));
        }
    }
}
