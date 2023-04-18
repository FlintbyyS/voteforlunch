package ru.flint.voteforlunch.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.flint.voteforlunch.model.Restaurant;
import ru.flint.voteforlunch.repository.RestaurantRepository;

import java.util.List;

import static ru.flint.voteforlunch.util.ValidationUtil.checkExist;
import static ru.flint.voteforlunch.util.ValidationUtil.checkFound;

@Service
@Slf4j
public class RestaurantService {
    private final RestaurantRepository repository;

    public RestaurantService(RestaurantRepository repository) {
        this.repository = repository;
    }

    public Restaurant getById(long id){
        log.info("Get restaurant with id = {}", id);
        return checkFound(repository.findById(id),id, Restaurant.class);
    }

    public List<Restaurant> getAllSorted(){
        log.info("Get all restaurants");
        return repository.findAll(Sort.by("name"));
    }

    public Restaurant create(Restaurant restaurant){
        log.info("Create restaurant: {}", restaurant);
        return repository.save(restaurant);
    }

    public void delete(long id){
        log.info("Delete restaurant with id = {}",id);
        repository.deleteById(id);
    }

    @Transactional
    public Restaurant update(long id,Restaurant restaurant){
        log.info("Update restaurant with id = {}", restaurant.getId());
        checkExist(repository.existsById(id),id, Restaurant.class);
        restaurant.setId(id);
        return repository.save(restaurant);
    }
}
