package com.us.spaces.hn;

import com.us.framework.model.DefaultHttpResponse;
import com.us.framework.model.HttpRequest;
import com.us.framework.model.HttpResponse;
import com.us.framework.model.RequestHandler;

import java.util.List;

public class StoryHandler implements RequestHandler {
    @Override
    public HttpResponse handle(HttpRequest req) {
        return switch (req.pathParam("type")) {
            case "top" -> new DefaultHttpResponse(topStory());
            case "new" -> new DefaultHttpResponse(newStory());
            default -> throw new RuntimeException(req.pathParam("type") + " not found");
        };
    }

    private List<Story> newStory() {
        return List.of(new Story(1, "me", "New story", null, "New story text", 100, 100, 1L));
    }

    private List<Story> topStory() {
        return List.of(new Story(1, "me", "Top story", null, "Top story text", 100, 100, 1L));
    }

}
