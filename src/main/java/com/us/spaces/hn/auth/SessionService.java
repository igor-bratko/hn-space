package com.us.spaces.hn.auth;

import com.us.framework.model.db.Db;
import com.us.framework.model.server.JsonConverter;

import java.nio.ByteBuffer;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

public class SessionService {
    private final Db db;
    private final JsonConverter converter;

    public SessionService(Db db, JsonConverter converter) {
        this.db = db;
        this.converter = converter;
    }

    public String createSession(String userId, String username) {
        var id = UUID.randomUUID().toString();

        var session = new Session(id, userId, username);
        var value = new String(converter.serialize(session).array());

        var updated = db.update("INSERT INTO key_value(key, value) VALUES (?, ?)", id, value);
        if (updated != 1) {
            throw new RuntimeException("Saving session failed");
        }

        return id;
    }

    public boolean deleteSession(String sessionId) {
        var updated = db.update("DELETE FROM key_value WHERE key=?", sessionId);
        if (updated != 1) {
            throw new RuntimeException("Deleting session failed");
        }
        return true;
    }

    public Session get(String sessionId) {
        requireNonNull(sessionId);

        return db.queryRow("SELECT * FROM key_value WHERE key=?", rs -> {
            var sessionJson = rs.getString("value");
            return converter.deserialize(ByteBuffer.wrap(sessionJson.getBytes()), Session.class);
        }, sessionId);
    }

    public record Session(String id, String userId, String username) {
    }
}
