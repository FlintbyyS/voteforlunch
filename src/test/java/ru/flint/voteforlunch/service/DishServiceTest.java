package ru.flint.voteforlunch.service;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;
import ru.flint.voteforlunch.util.exceptions.IllegalRequestDataException;
import ru.flint.voteforlunch.model.Dish;
import ru.flint.voteforlunch.repository.DishRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.when;


class DishServiceTest {
    private DishService service;

    @Mock
    private DishRepository repository;

    @Captor
    ArgumentCaptor<Dish> dishCaptor;

    @Captor
    ArgumentCaptor<Long> idCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new DishService(repository);
    }

    @Nested
    class FindDishes {
        @Test
        void find() {
            Dish expectedDish = Instancio.create(Dish.class);
            assert expectedDish.getId() != null;
            when(repository.findById(expectedDish.getId())).thenReturn(Optional.of(expectedDish));

            Dish actualDish = service.getById(expectedDish.getId());

            assertThat(actualDish).usingRecursiveComparison().isEqualTo(expectedDish);
        }

        @Test
        void throwWhenFindNotExisted() {
            when(repository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.getById(1L))
                    .isInstanceOf(IllegalRequestDataException.class)
                    .hasMessageContaining(String.format("Dish with id = %d not found", 1L));
        }

        @Test
        void findAll() {
            Dish dish1 = Instancio.create(Dish.class);
            dish1.setName("Zebra pie");
            Dish dish2 = Instancio.create(Dish.class);
            dish2.setName("Apple pie");
            when(repository.findAll(Sort.by("name"))).thenReturn(List.of(dish2, dish1));

            assertThat(service.getAllSorted()).usingRecursiveComparison().isEqualTo(List.of(dish2, dish1));
        }
    }

    @Nested
    class CreateDish {
        @Test
        void create() {
            Dish dish = Instancio.create(Dish.class);
            dish.setId(null);

            service.create(dish);
            then(repository).should().save(dishCaptor.capture());

            assertThat(dishCaptor.getValue())
                    .usingRecursiveComparison()
                    .ignoringFields("id")
                    .isEqualTo(dish);
        }
    }

    @Nested
    class DeleteDish {
        @Test
        void delete() {
            service.delete(1L);
            then(repository).should().deleteById(idCaptor.capture());

            assertThat(idCaptor.getValue()).isEqualTo(1L);
        }
    }

    @Nested
    class UpdateDish {
        @Test
        void update() {
            Dish dish = Instancio.create(Dish.class);
            when(repository.existsById(dish.getId())).thenReturn(true);

            Dish updatedDish = Instancio.create(Dish.class);
            service.update(dish.getId(), updatedDish);
            then(repository).should().save(dishCaptor.capture());

            updatedDish.setId(dish.getId());
            assertThat(dishCaptor.getValue()).usingRecursiveComparison().isEqualTo(updatedDish);
        }

        @Test
        void throwWhenUpdateWrongId() {
            when(repository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.update(1L, new Dish()))
                    .isInstanceOf(IllegalRequestDataException.class)
                    .hasMessageContaining(String.format("Dish with id = %d not found", 1L));
        }
    }
}
