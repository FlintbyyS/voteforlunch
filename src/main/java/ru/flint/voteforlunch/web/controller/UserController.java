package ru.flint.voteforlunch.web.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.flint.voteforlunch.dto.UserDTO;
import ru.flint.voteforlunch.mapper.UserMapper;
import ru.flint.voteforlunch.model.User;
import ru.flint.voteforlunch.service.UserService;
import ru.flint.voteforlunch.web.security.AuthorizedUser;

import java.net.URI;
import java.util.List;

import static ru.flint.voteforlunch.util.ValidationUtil.checkNew;

@RestController
@RequestMapping(value = UserController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    public static final String REST_URL = "/api/version1.0/users";

    private final UserService service;
    private final UserMapper mapper;

    public UserController(UserService service, UserMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public List<UserDTO> getAll() {
        return service.getAllSorted().stream().map(mapper::toDto).toList();
    }

    @GetMapping("/{id}")
    public UserDTO get(@PathVariable long id) {
        return mapper.toDto(service.getById(id));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> createWithLocation(@Valid @RequestBody UserDTO userDTO) {
        checkNew(userDTO);
        User created = service.create(mapper.toEntity(userDTO));
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(mapper.toDto(created));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        service.delete(id);
    }
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserDTO update(@Valid @RequestBody UserDTO userDTO, @PathVariable long id) {
        return mapper.toDto(service.update(id,mapper.toEntity(userDTO)));
    }

    @GetMapping("/profile")
    public UserDTO getProfile(@AuthenticationPrincipal AuthorizedUser user) {
        return get(user.id());
    }

    @PutMapping(path = "/profile", consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserDTO updateProfile(@AuthenticationPrincipal AuthorizedUser user, @Valid @RequestBody UserDTO userDTO) {
        return update(userDTO, user.id());
    }
}
