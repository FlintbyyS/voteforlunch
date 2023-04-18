package ru.flint.voteforlunch.web.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.flint.voteforlunch.dto.MenuListDTO;
import ru.flint.voteforlunch.dto.MenuRequestDTO;
import ru.flint.voteforlunch.dto.MenuResponseDTO;
import ru.flint.voteforlunch.mapper.MenuListMapper;
import ru.flint.voteforlunch.mapper.MenuMapper;
import ru.flint.voteforlunch.model.Menu;
import ru.flint.voteforlunch.service.MenuService;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static ru.flint.voteforlunch.util.ValidationUtil.checkNew;

@RestController
@RequestMapping(value = MenuController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class MenuController {
    public static final String REST_URL = "/api/version1.0/menus";

    private final MenuService service;
    private final MenuMapper mapper;
    private final MenuListMapper listMapper;

    public MenuController(MenuService service, MenuMapper mapper, MenuListMapper listMapper) {
        this.service = service;
        this.mapper = mapper;
        this.listMapper = listMapper;
    }

    @GetMapping("/on-date")
    @ResponseStatus(HttpStatus.OK)
    public List<MenuListDTO> getMenusOnDate(
            @RequestParam(name = "date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NotNull LocalDate date){
        return service.getAllWithRestaurants(date).stream().map(listMapper::toDTO).toList();
    }

    @GetMapping("/{id}")
    public MenuResponseDTO get (@PathVariable long id){
        return mapper.toDTO(service.getByIdWithAllData(id));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<MenuResponseDTO> createWithLocation(@Valid @RequestBody MenuRequestDTO menuDTO) {
        checkNew(menuDTO);
        Menu created = service.create(mapper.toEntity(menuDTO));
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(mapper.toDTO(created));
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        service.delete(id);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<MenuResponseDTO> update(@Valid @RequestBody MenuRequestDTO menuDTO, @PathVariable long id) {
        return ResponseEntity.ok(mapper.toDTO(service.update(id, mapper.toEntity(menuDTO))));
    }
}
