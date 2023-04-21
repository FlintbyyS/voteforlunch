package ru.flint.voteforlunch.model;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import ru.flint.voteforlunch.util.annotation.NoHtml;
import ru.flint.voteforlunch.web.security.PasswordDeserializer;

import java.io.Serializable;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

@Entity
@Table(name = "users", indexes = @Index(name = "email_unique_idx", columnList = "email", unique = true))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true, exclude = {"password"})
public class User extends AbstractEntity implements Serializable{
    @Column(name = "email", nullable = false)
    @Email(message = "Enter valid e-mail")
    @NoHtml
    @Size(max = 128)
    private String email;

    @Column(name = "first_name")
    @Size(max = 128)
    @NoHtml
    @NotBlank(message = "First name must not be empty")
    private String firstName;

    @Column(name = "last_name")
    @Size(max = 128)
    @NoHtml
    @NotBlank(message = "Last name must not be empty")
    private String lastName;

    @Column(name = "password", nullable = false)
    @NotBlank(message = "Password must not be empty")
    @Size(min = 5, max = 256)
    // https://stackoverflow.com/a/12505165/548473
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JsonDeserialize(using = PasswordDeserializer.class)
    private String password;


    private boolean enabled = true;


    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"),
            uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "role"}, name = "uk_user_role")})
    @Column(name = "role")
    @ElementCollection(fetch = FetchType.EAGER)
    @NotEmpty(message = "Roles must not be empty")
    private Set<Role> roles;

    public void setEmail(String email) {
        this.email = StringUtils.hasText(email) ? email.toLowerCase() : null;
    }
    public Set<Role> getRoles() {
        return roles;
    }
    public void setRoles(Collection<Role> roles) {
        this.roles = CollectionUtils.isEmpty(roles) ? EnumSet.noneOf(Role.class) : EnumSet.copyOf(roles);
    }
}
