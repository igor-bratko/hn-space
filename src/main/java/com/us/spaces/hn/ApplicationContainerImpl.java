package com.us.spaces.hn;

import com.us.framework.model.ApplicationContainer;
import com.us.framework.model.db.DbConfig;
import com.us.framework.model.server.HttpRoute;

import java.util.ArrayList;
import java.util.List;

public class ApplicationContainerImpl extends ApplicationContainer {

    @Override
    public List<HttpRoute> buildRoutes() {
        var db = initDb();

        var storyRepo = new StoryRepository(db);
        var storyController = new StoryController(storyRepo);

        var routes = new ArrayList<HttpRoute>();
        routes.add(new HttpRoute("POST", "/api/stories", storyController::createStory));
        routes.add(new HttpRoute("GET", "/api/stories/{storyId}", storyController::getStory));
        routes.add(new HttpRoute("POST", "/api/stories/{storyId}/comments", storyController::postComment));
        routes.add(new HttpRoute("GET", "/api/stories/{storyId}/comments", storyController::getStoryComments));
        routes.add(new HttpRoute("GET", "/api/stories/type/{type}", storyController::getStories));

        return routes;
    }

    @Override
    public DbConfig dbConfig() {
        return new DbConfig("hikari.properties");
    }

    @Override
    public int getPort() {
        return 8080;
    }
}
