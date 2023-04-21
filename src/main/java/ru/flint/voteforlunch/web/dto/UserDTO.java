package ru.flint.voteforlunch.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Value;
import ru.flint.voteforlunch.model.Role;
import ru.flint.voteforlunch.util.annotation.NoHtml;


import java.util.Set;

@Value
@EqualsAndHashCode(callSuper = true)
public class UserDTO extends AbstractDTO {
    @Size(max = 128)
    @NoHtml
    @Email(message = "Please enter valid e-mail")
    String email;

    @Size(max = 128)
    @NoHtml
    @NotBlank(message = "First name must not be empty")
    String firstName;

    @Size(max = 128)
    @NoHtml
    @NotBlank(message = "Last name must not be empty")
    String lastName;

    @Size(max = 256)
    @NotBlank(message = "Password must not be empty")
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    String password;

    boolean enabled;

    @NotEmpty(message = "Roles must not be empty")
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    Set<Role> roles;

    public UserDTO(Long id, String email, String firstName, String lastName, String password, boolean enabled, Set<Role> roles) {
        super(id);
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.enabled = enabled;
        this.roles = roles;
    }
}
