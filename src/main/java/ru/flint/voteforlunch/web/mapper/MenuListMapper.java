package ru.flint.voteforlunch.web.mapper;

import org.springframework.stereotype.Component;
import ru.flint.voteforlunch.web.dto.MenuListDTO;
import ru.flint.voteforlunch.model.Menu;

@Component
public class MenuListMapper implements Mapper<Menu, MenuListDTO> {
    @Override
    public Menu toEntity(MenuListDTO dto) {
        throw new UnsupportedOperationException("Convert MenuListDto to Menu is not supported");
    }

    @Override
    public MenuListDTO toDTO(Menu entity) {
        return new MenuListDTO(
                entity.getId(),
                entity.getMenuDate(),
                entity.getRestaurant().getId(),
                entity.getRestaurant().getName()
        );
    }
}
