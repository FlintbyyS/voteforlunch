package ru.flint.voteforlunch.web.dto;

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
public class MenuItemRequestDTO extends AbstractDTO{
    long dishId;

    @Positive Integer price;
}
