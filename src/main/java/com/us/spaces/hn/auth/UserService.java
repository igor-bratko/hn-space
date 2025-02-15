package com.us.spaces.hn.auth;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User login(String username, String password) {
        var user = userRepository.getByName(username);

        var valid = PasswordHasher.verifyPassword(password, user.password());
        if (!valid) {
            throw new RuntimeException("Wrong password");
        }

        return user;
    }
}
