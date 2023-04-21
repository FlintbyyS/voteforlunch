package ru.flint.voteforlunch.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import ru.flint.voteforlunch.util.annotation.NoHtml;

/**
 * A DTO for the {@link ru.flint.voteforlunch.model.Restaurant} entity
 */
@Value
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RestaurantDTO extends AbstractDTO{
    @NotBlank(message = "The restaurant must have a name")
    @NoHtml
    String name;

    public RestaurantDTO(Long id, String name) {
        super(id);
        this.name = name;
    }
}
