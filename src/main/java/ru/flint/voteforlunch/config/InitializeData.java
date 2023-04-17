package ru.flint.voteforlunch.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.flint.voteforlunch.model.Role;
import ru.flint.voteforlunch.model.User;
import ru.flint.voteforlunch.repository.UserRepository;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InitializeData implements CommandLineRunner {
    private final UserRepository userRepository;
    private final Clock clock;
    public InitializeData(UserRepository userRepository, Clock clock) {
        this.userRepository = userRepository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        // pre-defined users for testing purposes
        User admin = new User("admin@ya.ru", "admin", "admin", "{noop}admin", true, Collections.singleton(Role.ADMIN));
        User user = new User("user@ya.ru", "user", "user", "{noop}user", true, Collections.singleton(Role.USER));
        // collection of users to create large amount of votes for run-time testing using curl
        List<User> userList = new ArrayList<>(2002);
        userList.add(admin);
        userList.add(user);
        for (int i = 0; i < 2000; i++) {
            userList.add(new User("user" + i + "@gmail.com", "userName" + i, "userSurname" + i, "{noop}user" + i, true, Collections.singleton(Role.USER)));
        }
        userRepository.saveAll(userList);
    }
}
