package ru.flint.voteforlunch.web.mapper;

import org.springframework.stereotype.Component;
import ru.flint.voteforlunch.web.dto.MenuItemRequestDTO;
import ru.flint.voteforlunch.web.dto.MenuItemResponseDTO;
import ru.flint.voteforlunch.model.MenuItem;
import ru.flint.voteforlunch.repository.DishRepository;

@Component
public class MenuItemMapper implements RequestResponseMapper<MenuItem, MenuItemRequestDTO, MenuItemResponseDTO>{
    private final DishMapper dishMapper;

    private final DishRepository dishRepository;

    public MenuItemMapper(DishMapper dishMapper, DishRepository dishRepository) {
        this.dishMapper = dishMapper;
        this.dishRepository = dishRepository;
    }
    @Override
    public MenuItem toEntity(MenuItemRequestDTO dto) {
        MenuItem menuItem = new MenuItem();
        menuItem.setId(dto.getId());
        menuItem.setDish(dishRepository.getReferenceById(dto.getDishId()));
        menuItem.setPrice(dto.getPrice());
        return menuItem;
    }

    @Override
    public MenuItemResponseDTO toDTO(MenuItem entity) {
        return new MenuItemResponseDTO(entity.getId(), dishMapper.toDTO(entity.getDish()), entity.getPrice());
    }
}
