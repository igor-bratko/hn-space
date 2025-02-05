package com.us.spaces.hn;

import com.us.framework.model.server.HttpRequest;
import com.us.framework.model.server.HttpResponse;
import com.us.spaces.hn.Story.PostComment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoryController {
    private static final Logger log = LoggerFactory.getLogger(StoryController.class);

    private final StoryRepository storyService;

    public StoryController(StoryRepository storyService) {
        this.storyService = storyService;
    }

    public HttpResponse getStories(HttpRequest req) {
        try {
            var stories = storyService.getStories(req.pathParams().get("type"));

            return new HttpResponse().body(stories).status(200);
        } catch (Exception e) {
            log.error("Failed to get stories.", e);

            var error = new ErrorDto("Get story failed ", e.getMessage());

            return new HttpResponse().body(error).status(500);
        }
    }

    public HttpResponse createStory(HttpRequest req) {
        try {
            var body = req.body().bodyAs(CreateStoryRequest.class);
            var storyId = storyService.createStory(body);

            return new HttpResponse().body(new CreateStoryResponse(storyId)).status(200);
        } catch (Exception e) {
            log.error("Failed to create story.", e);

            var error = new ErrorDto("Create story failed ", e.getMessage());

            return new HttpResponse().body(error).status(500);
        }
    }

    public HttpResponse getStory(HttpRequest req) {
        try {
            var storyId = req.pathParams().get("storyId");
            var story = storyService.getStory(storyId);

            return new HttpResponse().body(story).status(200);
        } catch (Exception e) {
            log.error("Failed to fetch story.", e);

            var error = new ErrorDto("Failed to fetch story ", e.getMessage());

            return new HttpResponse().body(error).status(500);
        }
    }

    public HttpResponse getStoryComments(HttpRequest req) {
        try {
            var storyId = req.pathParams().get("storyId");
            var storyComments = storyService.getStoryComments(storyId);

            return new HttpResponse().body(storyComments).status(200);
        } catch (Exception e) {
            log.error("Failed to fetch story.", e);

            var error = new ErrorDto("Failed to fetch story ", e.getMessage());

            return new HttpResponse().body(error).status(500);
        }
    }

    public HttpResponse postComment(HttpRequest req) {
        try {
            var storyId = req.pathParams().get("storyId");
            var body = req.body().bodyAs(PostComment.class);

            var commentId = storyService.addComment(storyId,body);

            return new HttpResponse().body(commentId).status(200);
        } catch (Exception e) {
            log.error("Failed to add comment.", e);

            var error = new ErrorDto("Failed to add comment ", e.getMessage());

            return new HttpResponse().body(error).status(500);
        }
    }

    public record ErrorDto(String message, String details) {
    }
}
