package ru.flint.voteforlunch.service;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.flint.voteforlunch.model.Menu;
import ru.flint.voteforlunch.repository.MenuRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static ru.flint.voteforlunch.util.ValidationUtil.checkExist;
import static ru.flint.voteforlunch.util.ValidationUtil.checkFound;

@Service
@Slf4j
public class MenuService {
    private final MenuRepository repository;

    public MenuService(MenuRepository repository) {
        this.repository = repository;
    }

    @Caching(
            put = {@CachePut(key = "#result.id", value = "menu")},
            evict = {@CacheEvict(key = "#result.menuDate", value = "menus")}
    )
    public Menu create(Menu menu) {
        log.info("Create menu: {}", menu);
        return repository.save(menu);
    }

    @Cacheable(key = "#date", value = "menus")
    public List<Menu> getAllWithRestaurants(@NotNull LocalDate date) {
        log.info("Get all menus with restaurants on {}", date);
        return repository.findAllWithRestaurantsOnDate(date);
    }
    @Transactional
    @Caching(
            put = {@CachePut(key = "#result.id", value = "menu")},
            evict = {@CacheEvict(key = "#result.menuDate", value = "menus")}
    )
    public Menu update(long id,Menu menu){
        log.info("Update menu with id = {}", menu.getId());
        checkExist(repository.existsById(id),id, Menu.class);
        menu.setId(id);
        Menu updatedMenu = repository.save(menu);
        return getByIdWithAllData(Objects.requireNonNull(updatedMenu.getId()));
    }

    @Caching(
            evict = {
                    @CacheEvict(key = "#id",value = "menu"),
                    @CacheEvict(value = "menus",allEntries = true)
            }
    )
    public void delete(long id){
        log.info("Delete menu with id = {}", id);
        repository.deleteById(id);
    }

    @Cacheable(key = "#id",value = "menu")
    public Menu getByIdWithAllData(long id){
        log.info("Get menu (including properties) with id = {}", id);
        return checkFound(repository.findAllByIdWithAllData(id),id,Menu.class);
    }
}
