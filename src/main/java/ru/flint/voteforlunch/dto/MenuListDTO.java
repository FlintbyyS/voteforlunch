package ru.flint.voteforlunch.dto;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

import java.time.LocalDate;

/**
 * A DTO for the {@link ru.flint.voteforlunch.model.Menu} entity
 */
@Value
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MenuListDTO extends AbstractDTO{
    @NotNull LocalDate menuDate;
    long restaurantId;
    @NotNull String restaurantName;

    public MenuListDTO(Long id, LocalDate menuDate, long restaurantId, String restaurantName) {
        super(id);
        this.menuDate = menuDate;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
    }
}
