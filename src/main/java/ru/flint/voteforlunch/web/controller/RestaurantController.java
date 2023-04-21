package ru.flint.voteforlunch.web.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.flint.voteforlunch.web.dto.RestaurantDTO;
import ru.flint.voteforlunch.web.mapper.RestaurantMapper;
import ru.flint.voteforlunch.model.Restaurant;
import ru.flint.voteforlunch.service.RestaurantService;

import java.net.URI;
import java.util.List;

import static ru.flint.voteforlunch.util.ValidationUtil.checkNew;

@RestController
@RequestMapping(value = RestaurantController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class RestaurantController {
    public static final String REST_URL = "/api/version1.0/restaurants";

    private final RestaurantService service;
    private final RestaurantMapper mapper;

    public RestaurantController(RestaurantService service, RestaurantMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public List<RestaurantDTO> getAll(){
        return service.getAllSorted().stream().map(mapper::toDTO).toList();
    }

    @GetMapping("/{id}")
    public RestaurantDTO get (@PathVariable long id){
        return mapper.toDTO(service.getById(id));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestaurantDTO> create(@Valid @RequestBody RestaurantDTO restaurantDto) {
        checkNew(restaurantDto);
        Restaurant created = service.create(mapper.toEntity(restaurantDto));
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(mapper.toDTO(created));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        service.delete(id);
    }
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public RestaurantDTO update(@Valid @RequestBody RestaurantDTO dishDTO, @PathVariable long id) {
        return mapper.toDTO(service.update(id,mapper.toEntity(dishDTO)));
    }
}
