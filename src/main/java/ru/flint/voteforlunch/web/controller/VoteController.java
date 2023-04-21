package ru.flint.voteforlunch.web.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.flint.voteforlunch.web.dto.VoteDTO;
import ru.flint.voteforlunch.web.mapper.VoteMapper;
import ru.flint.voteforlunch.model.VoteDistribution;
import ru.flint.voteforlunch.service.VoteService;
import ru.flint.voteforlunch.web.security.AuthorizedUser;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(value = VoteController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class VoteController {
    public static final String REST_URL = "/api/version1.0/votes";

    private final VoteService service;
    private final VoteMapper mapper;

    public VoteController(VoteService service, VoteMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public List<VoteDTO> getAllForUser(@AuthenticationPrincipal AuthorizedUser authorizedUser){
        return service.getAllForUser(authorizedUser.id()).stream().map(mapper::toDTO).toList();
    }
    @GetMapping("/{id}")
    public VoteDTO getVote(@PathVariable long id,@AuthenticationPrincipal AuthorizedUser authorizedUser){
        return mapper.toDTO(service.get(id,authorizedUser.id()));
    }

    @GetMapping("/distribution")
    @ResponseStatus(HttpStatus.OK)
    public List<VoteDistribution> getDistributionOnDate(
            @RequestParam(name = "date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){
        return service.getDistributionOnDate(date);
    }

    @PostMapping
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    public VoteDTO vote(@RequestParam("restaurantId") long restaurantId
            , @AuthenticationPrincipal AuthorizedUser authorizedUser){
       return mapper.toDTO(service.saveAndReturnWithDetails(restaurantId,authorizedUser.id()));
    }

    @PutMapping
    @Transactional
    public VoteDTO changeVote(@RequestParam("restaurantId") long restaurantId
            , @AuthenticationPrincipal AuthorizedUser authorizedUser){
        return mapper.toDTO(service.saveAndReturnWithDetails(restaurantId,authorizedUser.id()));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal AuthorizedUser authUser) {
        service.delete(authUser.id());
    }
}
