package com.us.spaces.hn.story;

import com.us.framework.model.db.Db;
import com.us.spaces.hn.story.Story.Comment;
import com.us.spaces.hn.story.Story.PostComment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class StoryRepository {
    private final Db db;

    public StoryRepository(Db db) {
        this.db = db;
    }

    public int createStory(CreateStoryRequest req) {
        var url = (req.url() == null || req.url().isBlank()) ? null : req.url();
        var text = (req.text() == null || req.text().isBlank()) ? null : req.text();

        return db.queryRow("""
                               INSERT INTO story (id, author, title, url, text, time, score, comment_cnt)
                               VALUES (
                                     ?,
                                     ?,
                                     ?,
                                     ?,
                                     ?,
                                     EXTRACT(EPOCH FROM current_timestamp),
                                     0,
                                     0
                                    )
                               RETURNING id;
                        """, rs -> rs.getInt("id"),
                ((Long) System.currentTimeMillis()).intValue(), req.author(), req.title(), url, text);
    }

    public List<Story> getStories(String type) {
        return switch (type) {
            case "top" -> handleTop();
            case "new" -> handleNew();
            default -> throw new IllegalArgumentException("Type " + type + " not found");
        };
    }

    private List<Story> handleNew() {
        return db.query("SELECT * FROM story ORDER BY time DESC", this::storyMapper);
    }

    private List<Story> handleTop() {
        return db.query("SELECT * FROM story ORDER BY comment_cnt DESC", this::storyMapper);
    }

    public Story getStory(String storyId) {
        return db.queryRow("SELECT * FROM story WHERE id=?", this::storyMapper, Integer.parseInt(storyId));
    }

    public List<Comment> getStoryComments(String storyId) {
        var sql = """
                SELECT * FROM
                comment c.
                WHERE story_id = ?
                """;

        return db.query(
                sql,
                rs -> new Comment(
                        rs.getInt("id"),
                        rs.getInt("parent"),
                        rs.getString("author"),
                        rs.getString("text"),
                        rs.getInt("time")),
                Integer.parseInt(storyId));
    }

    private Story storyMapper(ResultSet rs) throws SQLException {
        return new Story(
                rs.getInt("id"),
                rs.getString("author"),
                rs.getString("title"),
                rs.getString("url"),
                rs.getString("text"),
                rs.getInt("score"),
                rs.getInt("comment_cnt"),
                rs.getLong("time")
        );
    }

    public int addComment(String storyId, PostComment com) {
        return db.runInTransaction(tx -> {
            var sql = """
                    INSERT INTO comment (author, story_id, time, parent, text)
                    VALUES (
                          ?,
                          ?,
                          EXTRACT(EPOCH FROM current_timestamp),
                          ?,
                          ?)
                    RETURNING id;
                    """;

            var commentId = tx.queryRow(
                    sql, rs -> rs.getInt("id"), com.author(), Integer.parseInt(storyId), com.parent(), com.text());


            tx.update("""
                    UPDATE story s
                    SET comment_cnt = (SELECT COUNT(id) FROM comment WHERE story_id = ?)
                    WHERE id = ?
                    """, Integer.parseInt(storyId), Integer.parseInt(storyId));

            return commentId;
        });
    }
}
