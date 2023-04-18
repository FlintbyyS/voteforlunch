package ru.flint.voteforlunch.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

/**
 * A DTO for the {@link ru.flint.voteforlunch.model.MenuItem} entity
 */
@Value
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MenuItemResponseDTO extends AbstractDTO{
    @NotNull DishDTO dishDTO;

    @Positive(message = "Price must be possitive")
    Integer price;

    public MenuItemResponseDTO(Long id, DishDTO dishDTO, int price){
        super(id);
        this.dishDTO = dishDTO;
        this.price = price;
    }
}
