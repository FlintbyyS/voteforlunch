package ru.flint.voteforlunch.web.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

import java.time.LocalDate;
import java.util.Set;

/**
 * A DTO for the {@link ru.flint.voteforlunch.model.Menu} entity
 */
@Value
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MenuRequestDTO extends AbstractDTO{
    @NotNull LocalDate menuDate;

    long restaurantId;

    @NotEmpty
    Set<MenuItemRequestDTO> menuItems;
}
