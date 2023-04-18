package ru.flint.voteforlunch.mapper;

import org.springframework.stereotype.Component;
import ru.flint.voteforlunch.dto.RestaurantDTO;
import ru.flint.voteforlunch.model.Restaurant;

@Component
public class RestaurantMapper implements Mapper<Restaurant, RestaurantDTO> {
    @Override
    public Restaurant toEntity(RestaurantDTO dto) {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(dto.getId());
        restaurant.setName(dto.getName());
        return restaurant;
    }

    @Override
    public RestaurantDTO toDTO(Restaurant entity) {
        return new RestaurantDTO(entity.getId(), entity.getName());
    }
}
