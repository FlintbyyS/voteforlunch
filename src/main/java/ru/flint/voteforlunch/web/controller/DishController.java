package ru.flint.voteforlunch.web.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.flint.voteforlunch.dto.DishDTO;
import ru.flint.voteforlunch.mapper.DishMapper;
import ru.flint.voteforlunch.model.Dish;
import ru.flint.voteforlunch.service.DishService;

import java.net.URI;
import java.util.List;

import static ru.flint.voteforlunch.util.ValidationUtil.checkNew;


@RestController
@RequestMapping(value = DishController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class DishController {
    public static final String REST_URL = "/api/version1.0/dishes";

    private final DishService service;
    private final DishMapper mapper;
    public DishController(DishService service, DishMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public List<DishDTO> getAll(){
        return service.getAllSorted().stream().map(mapper::toDTO).toList();
    }

    @GetMapping("/{id}")
    public DishDTO get (@PathVariable long id){
        return mapper.toDTO(service.getById(id));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DishDTO> createWithLocation(@Valid @RequestBody DishDTO dishDTO) {
        checkNew(dishDTO);
        Dish created = service.create(mapper.toEntity(dishDTO));
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
    public DishDTO update(@Valid @RequestBody DishDTO dishDTO, @PathVariable long id) {
        return mapper.toDTO(service.update(id,mapper.toEntity(dishDTO)));
    }
}
