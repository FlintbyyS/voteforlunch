package ru.flint.voteforlunch.web.mapper;

import org.springframework.stereotype.Component;
import ru.flint.voteforlunch.web.dto.VoteDTO;
import ru.flint.voteforlunch.model.Vote;

@Component
public class VoteMapper implements Mapper<Vote, VoteDTO> {
    private final RestaurantMapper restaurantMapper;

    public VoteMapper(RestaurantMapper restaurantMapper) {
        this.restaurantMapper = restaurantMapper;
    }
    @Override
    public Vote toEntity(VoteDTO dto) {
        Vote vote = new Vote();
        vote.setId(dto.getId());
        vote.setVoteDate(dto.getVoteDate());
        vote.setVoteTime(dto.getVoteTime());
        vote.setRestaurant(restaurantMapper.toEntity(dto.getRestaurant()));
        return vote;
    }

    @Override
    public VoteDTO toDTO(Vote entity) {
        return new VoteDTO(
                entity.getId(),
                restaurantMapper.toDTO(entity.getRestaurant()),
                entity.getVoteDate(),
                entity.getVoteTime()
        );
    }
}
