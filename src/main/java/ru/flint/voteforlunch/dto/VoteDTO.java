package ru.flint.voteforlunch.dto;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.time.LocalDate;
import java.time.LocalTime;

@Value
@EqualsAndHashCode(callSuper = true)
public class VoteDTO extends AbstractDTO{
    @NotNull RestaurantDTO restaurant;

    @NotNull LocalDate voteDate;

    @NotNull LocalTime voteTime;

    public VoteDTO(Long id, RestaurantDTO restaurant, LocalDate voteDate, LocalTime voteTime) {
        super(id);
        this.restaurant = restaurant;
        this.voteDate = voteDate;
        this.voteTime = voteTime;
    }
}
