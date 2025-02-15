package com.us.spaces.hn.story;

public record CreateStoryRequest(Integer id, String author, String title, String url, String text) {
}
