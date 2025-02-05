package com.us.spaces.hn;

public record CreateStoryRequest(Integer id, String author, String title, String url, String text) {
}
