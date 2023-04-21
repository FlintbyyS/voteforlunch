package ru.flint.voteforlunch.web.mapper;

import org.springframework.stereotype.Component;
import ru.flint.voteforlunch.web.dto.MenuItemRequestDTO;
import ru.flint.voteforlunch.web.dto.MenuRequestDTO;
import ru.flint.voteforlunch.web.dto.MenuResponseDTO;
import ru.flint.voteforlunch.model.Menu;
import ru.flint.voteforlunch.repository.RestaurantRepository;

import java.util.stream.Collectors;

@Component
public class MenuMapper implements RequestResponseMapper<Menu, MenuRequestDTO, MenuResponseDTO> {
    private final RestaurantMapper restaurantMapper;
    private final MenuItemMapper menuItemMapper;
    private final RestaurantRepository restaurantRepository;

    public MenuMapper(RestaurantMapper restaurantMapper, MenuItemMapper menuItemMapper, RestaurantRepository restaurantRepository) {
        this.restaurantMapper = restaurantMapper;
        this.menuItemMapper = menuItemMapper;
        this.restaurantRepository = restaurantRepository;
    }
    @Override
    public Menu toEntity(MenuRequestDTO dto) {
        Menu menu = new Menu();
        menu.setId(dto.getId());
        menu.setMenuDate(dto.getMenuDate());
        menu.setRestaurant(restaurantRepository.getReferenceById(dto.getRestaurantId()));
        for (MenuItemRequestDTO menuItemDTO : dto.getMenuItems()) {
            menu.addMenuItem(menuItemMapper.toEntity(menuItemDTO));
        }
        return menu;
    }

    @Override
    public MenuResponseDTO toDTO(Menu entity) {
        return new MenuResponseDTO(
                entity.getId(),
                entity.getMenuDate(),
                restaurantMapper.toDTO(entity.getRestaurant()),
                entity.getMenuItemSet().stream().map(menuItemMapper::toDTO).collect(Collectors.toSet()));
    }
}
