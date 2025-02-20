package com.us.spaces.hn;

import com.us.framework.model.ApplicationContainer;
import com.us.framework.model.db.DbConfig;
import com.us.framework.model.server.HttpRoute;
import com.us.spaces.hn.auth.*;
import com.us.spaces.hn.story.StoryController;
import com.us.spaces.hn.story.StoryRepository;

import java.util.ArrayList;
import java.util.List;

public class ApplicationContainerImpl extends ApplicationContainer {

    @Override
    public List<HttpRoute> buildRoutes() {
        var db = initDb();
        var jsonConverter = httpRequestJsonConverter();

        var storyRepo = new StoryRepository(db);
        var storyController = new StoryController(storyRepo);

        var userRepository = new UserRepository(db);
        var sessionService = new SessionService(db, jsonConverter);
        var userService = new UserService(userRepository);
        var authController = new AuthController(userRepository, sessionService, userService);
        var profileController = new ProfileController(userRepository);

        var routes = new ArrayList<HttpRoute>();
        routes.add(new HttpRoute("POST", "/api/stories", storyController::createStory));
        routes.add(new HttpRoute("GET", "/api/stories/{storyId}", storyController::getStory));
        routes.add(new HttpRoute("POST", "/api/stories/{storyId}/comments", storyController::postComment));
        routes.add(new HttpRoute("GET", "/api/stories/{storyId}/comments", storyController::getStoryComments));
        routes.add(new HttpRoute("GET", "/api/stories/type/{type}", storyController::getStories));
        routes.add(new HttpRoute("POST", "/api/auth/sign-up", authController::signUp));
        routes.add(new HttpRoute("POST", "/api/auth/sign-in", authController::signIn));
        routes.add(new HttpRoute("POST", "/api/auth/logout", authController::logout));
        routes.add(new HttpRoute("GET", "/api/me", authController::me));
        routes.add(new HttpRoute("GET", "/api/profile", profileController::getProfile));
        routes.add(new HttpRoute("PUT", "/api/profile", profileController::updateProfile));

        return routes;
    }

    @Override
    public DbConfig dbConfig() {
        return new DbConfig("hikari.properties");
    }

    @Override
    public int getPort() {
        return 8082;
    }
}
