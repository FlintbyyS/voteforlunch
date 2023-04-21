package ru.flint.voteforlunch.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import ru.flint.voteforlunch.util.annotation.NoHtml;
/**
 * A DTO for the {@link ru.flint.voteforlunch.model.Dish} entity
 */
@Value
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DishDTO extends AbstractDTO{
    @NotBlank(message = "The dish must have a name")
    @NoHtml
    String name;

    public DishDTO(Long id, String name) {
        super(id);
        this.name = name;
    }
}
