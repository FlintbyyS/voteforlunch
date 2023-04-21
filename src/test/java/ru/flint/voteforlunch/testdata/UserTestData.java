package ru.flint.voteforlunch.testdata;

import ru.flint.voteforlunch.model.Role;
import ru.flint.voteforlunch.model.User;
import ru.flint.voteforlunch.utils.MatcherFactory;
import ru.flint.voteforlunch.web.dto.UserDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class UserTestData {
    public static MatcherFactory.Matcher<UserDTO> USER_DTO_MATCHER = MatcherFactory.usingIgnoringFieldsComparator(UserDTO.class, "password");

    public static List<UserDTO> USER_DTO_LIST = new ArrayList<>(2002);

    public static final String ADMIN_MAIL = "admin@ya.ru";
    public static final String USER_MAIL = "user@ya.ru";
    public static User ADMIN = new User(ADMIN_MAIL, "admin", "admin", "admin", true, Collections.singleton(Role.ADMIN));
    public static User USER = new User("user@ya.ru", "user", "user", "user", true, Collections.singleton(Role.USER));
    public static UserDTO ADMIN_DTO = new UserDTO(1L, ADMIN.getEmail(), ADMIN.getFirstName(), ADMIN.getLastName(), "***", ADMIN.isEnabled(), ADMIN.getRoles());
    public static UserDTO USER_DTO = new UserDTO(2L, USER.getEmail(), USER.getFirstName(), USER.getLastName(), "***", USER.isEnabled(), USER.getRoles());

    static {
        ADMIN_DTO.setId(1L);
        USER_DTO.setId(2L);
        USER_DTO_LIST.add(ADMIN_DTO);
        USER_DTO_LIST.add(USER_DTO);
        for (long i = 0; i < 2000; i++) {
            UserDTO dto = new UserDTO(null, "user" + i + "@gmail.com", "userName" + i, "userSurname" + i, "***", true, Collections.singleton(Role.USER));
            dto.setId(i + 3);
            USER_DTO_LIST.add(dto);
        }
        USER_DTO_LIST = USER_DTO_LIST.stream().sorted(Comparator.comparing(UserDTO::getEmail)).toList();
    }

    public static UserDTO getNewDto() {
        return new UserDTO(null, "new@gmail.com", "проверим и юникод тоже", "newsurname",
                "***", true, Collections.singleton(Role.USER));
    }

    public static UserDTO getNewDto(String email) {
        return new UserDTO(null, email, "проверим и юникод тоже", "newsurname",
                "***", true, Collections.singleton(Role.USER));
    }

    public static UserDTO getUpdatedDto() {
        return getUpdatedDto(USER_DTO.getEmail());
    }

    public static UserDTO getUpdatedDto(String email) {
        UserDTO updatedDto = new UserDTO(
                USER_DTO.getId(),
                email,
                USER_DTO.getFirstName(),
                USER_DTO.getLastName(),
                USER_DTO.getPassword(),
                USER_DTO.isEnabled(),
                USER_DTO.getRoles()
        );
        updatedDto.setId(USER_DTO.getId());
        return updatedDto;
    }
}