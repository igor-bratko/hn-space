package com.us.spaces.hn;

public record Story(int id,
                    String author,
                    String title,
                    String url,
                    String text,
                    int score,
                    int commentsCnt,
                    long time) {

    public record Comment(int id, int parent, String author, String text, int time) {}
    public record PostComment(Integer id, String author, Integer parent, String text) {}
}


