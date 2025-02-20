package com.us.spaces.hn.auth;

import com.us.framework.model.db.Db;

import java.util.UUID;

public class UserRepository {
    private final Db db;

    public UserRepository(Db db) {
        this.db = db;
    }

    public String createUser(String username, String password) {
        var id = UUID.randomUUID().toString();

        var updated = db.update("""
                               INSERT INTO "user" (id, username, password)
                               VALUES ( ?, ?, ? );
                        """,
                id, username, password);

        if (updated == 1) {
            return id;
        } else {
            throw new RuntimeException("Failed to create user");
        }
    }

    public User getByName(String username) {
        var user = db.queryRow("""
                    SELECT * FROM "user" WHERE username=?
                """, rs -> {
            var id = rs.getString("id");
            var password = rs.getString("password");
            return new User(id, username, password);
        }, username);

        return user;
    }

    public UserProfile get(String userId) {
        try {
            return db.queryRow("""
                            SELECT * FROM "user"
                            WHERE id = ?
                            """,
                    rs1 -> {
                        var username = rs1.getString("username");
                        var karma = rs1.getInt("karma");
                        var about = rs1.getString("about");
                        var createdAt = rs1.getTimestamp("created_at").toInstant().getEpochSecond();
                        return new UserProfile(userId, username, karma, about, createdAt);
                    },
                    userId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
