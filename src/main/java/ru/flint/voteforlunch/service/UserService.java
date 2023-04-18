package ru.flint.voteforlunch.service;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.flint.voteforlunch.model.User;
import ru.flint.voteforlunch.repository.UserRepository;

import java.util.List;

import static ru.flint.voteforlunch.util.ValidationUtil.checkFound;

@Service
@Slf4j
public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository){
        this.repository = repository;
    }
    public User create(@NotNull User user) {
        log.info("Create user: {}",user);
        return repository.save(user);
    }

    public void delete(long id) {
        log.info("Delete user with id = {}",id);
        repository.deleteById(id);
    }

    public User getById(long id) {
        log.info("Get user with id = {}",id);
        return checkFound(repository.findById(id),id, User.class);
    }

    public List<User> getAllSorted() {
        log.info("Get all users");
        return repository.findAll(Sort.by("email"));
    }

    @Transactional
    public User update(long id, User user) {
        log.info("Update user with id = {}", user.getId());
        User storedUser = checkFound(repository.findById(id), id, User.class);
        user.setId(id);
        user.setPassword(storedUser.getPassword()); // do not update the password, it must be updated in a separate way
        user.setRoles(storedUser.getRoles()); // do not update roles, it must be updated in a separate way
        return repository.save(user);
    }
}
