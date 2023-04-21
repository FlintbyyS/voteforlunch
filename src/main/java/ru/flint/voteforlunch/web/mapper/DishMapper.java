package ru.flint.voteforlunch.web.mapper;

import org.springframework.stereotype.Component;
import ru.flint.voteforlunch.web.dto.DishDTO;
import ru.flint.voteforlunch.model.Dish;

@Component
public class DishMapper implements Mapper<Dish, DishDTO> {
    @Override
    public Dish toEntity(DishDTO dto) {
        Dish dish = new Dish();
        dish.setId(dto.getId());
        dish.setName(dto.getName());
        return dish;
    }

    @Override
    public DishDTO toDTO(Dish entity) {
        return new DishDTO(entity.getId(), entity.getName());
    }
}
