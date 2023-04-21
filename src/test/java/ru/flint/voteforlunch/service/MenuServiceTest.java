package ru.flint.voteforlunch.service;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.flint.voteforlunch.util.exceptions.IllegalRequestDataException;
import ru.flint.voteforlunch.model.Menu;
import ru.flint.voteforlunch.repository.MenuRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.when;

class MenuServiceTest {
    private MenuService service;

    @Mock
    private MenuRepository repository;

    @Captor
    ArgumentCaptor<Menu> MenuCaptor;

    @Captor
    ArgumentCaptor<Long> idCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new MenuService(repository);
    }

    @Nested
    class FindMenus {
        @Test
        void find() {
            Menu menu = Instancio.create(Menu.class);
            when(repository.findAllByIdWithAllData(1L)).thenReturn(Optional.of(menu));

            assertThat(service.getByIdWithAllData(1L)).usingRecursiveComparison().isEqualTo(menu);
        }

        @Test
        void throwWhenFindNotExisted() {
            when(repository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.getByIdWithAllData(1L))
                    .isInstanceOf(IllegalRequestDataException.class)
                    .hasMessageContaining(String.format("Menu with id = %d not found", 1L));
        }

        @Test
        void findAllWithRestaurantsOnDate() {
            Menu menu1 = Instancio.create(Menu.class);
            Menu menu2 = Instancio.create(Menu.class);
            Menu menu3 = Instancio.create(Menu.class);
            LocalDate dateOfMenu = LocalDate.of(2022, 10, 10);
            menu1.setMenuDate(dateOfMenu);
            menu2.setMenuDate(dateOfMenu.plusDays(1));
            menu3.setMenuDate(dateOfMenu);
            List<Menu> menuList = List.of(menu1, menu3);
            when(repository.findAllWithRestaurantsOnDate(dateOfMenu)).thenReturn(menuList);

            assertThat(service.getAllWithRestaurants(dateOfMenu)).usingRecursiveComparison().isEqualTo(menuList);
        }
    }

    @Nested
    class CreateMenu {
        @Test
        void create() {
            Menu Menu = Instancio.create(Menu.class);
            Menu.setId(null);

            service.create(Menu);
            then(repository).should().save(MenuCaptor.capture());

            assertThat(MenuCaptor.getValue())
                    .usingRecursiveComparison()
                    .ignoringFields("id")
                    .isEqualTo(Menu);
        }
    }

    @Nested
    class DeleteMenu {
        @Test
        void delete() {
            service.delete(1L);
            then(repository).should().deleteById(idCaptor.capture());

            assertThat(idCaptor.getValue()).isEqualTo(1L);
        }
    }

    @Nested
    class UpdateMenu {
        @Test
        void update() {
            Menu menu = Instancio.create(Menu.class);
            when(repository.existsById(menu.getId())).thenReturn(true);
            when(repository.save(menu)).thenReturn(menu);
            when(repository.findAllByIdWithAllData(menu.getId())).thenReturn(Optional.of(menu));

            Menu updatedMenu = Instancio.create(Menu.class);
            updatedMenu.setId(menu.getId());
            service.update(menu.getId(), updatedMenu);
            then(repository).should().save(MenuCaptor.capture());

            updatedMenu.setId(menu.getId());
            assertThat(MenuCaptor.getValue()).usingRecursiveComparison().isEqualTo(updatedMenu);
        }

        @Test
        void throwWhenUpdateWrongId() {
            when(repository.findById(1L)).thenReturn(Optional.empty());

            Menu menu = new Menu();
            menu.setId(1L);
            assertThatThrownBy(() -> service.update(1L, menu))
                    .isInstanceOf(IllegalRequestDataException.class)
                    .hasMessageContaining(String.format("Menu with id = %d not found", 1L));
        }
    }
}
