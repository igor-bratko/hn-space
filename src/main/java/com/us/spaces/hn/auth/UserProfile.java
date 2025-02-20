package com.us.spaces.hn.auth;

public record UserProfile(String userId,
                          String username,
                          int karma,
                          String about,
                          long createdAt) {
}