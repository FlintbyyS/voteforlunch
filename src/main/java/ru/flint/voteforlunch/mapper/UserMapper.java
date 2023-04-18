package ru.flint.voteforlunch.mapper;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.flint.voteforlunch.dto.UserDTO;
import ru.flint.voteforlunch.model.User;
@Component
public class UserMapper implements Mapper<User, UserDTO>{
    private final PasswordEncoder passwordEncoder;

    public UserMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User toEntity(UserDTO dto) {
        User user = new User(
                dto.getEmail(),
                dto.getFirstName(),
                dto.getLastName(),
                passwordEncoder.encode(dto.getPassword()),
                dto.isEnabled(),
                dto.getRoles()
        );
        user.setId(dto.getId());
        return user;
    }

    @Override
    public UserDTO toDTO(User entity) {
        return new UserDTO(
                entity.getId(),
                entity.getEmail(),
                entity.getFirstName(),
                entity.getLastName(),
                "***", // do not expose password to frontend
                entity.isEnabled(),
                entity.getRoles()
        );
    }
}
