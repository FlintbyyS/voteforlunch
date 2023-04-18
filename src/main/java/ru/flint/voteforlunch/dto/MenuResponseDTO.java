package ru.flint.voteforlunch.dto;

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
public class MenuResponseDTO extends AbstractDTO{
    @NotNull LocalDate menuDate;

    @NotNull RestaurantDTO restaurantDTO;

    Set<MenuItemResponseDTO> menuItems;

    public MenuResponseDTO(Long id, LocalDate menuDate, RestaurantDTO restaurantDTO, Set<MenuItemResponseDTO> menuItems) {
        super(id);
        this.menuDate = menuDate;
        this.restaurantDTO = restaurantDTO;
        this.menuItems = menuItems;
    }
}
