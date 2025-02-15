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
}
