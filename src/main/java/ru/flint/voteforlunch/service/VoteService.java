package ru.flint.voteforlunch.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.flint.voteforlunch.exceptions.IllegalRequestDataException;
import ru.flint.voteforlunch.exceptions.VoteTimeConstraintException;
import ru.flint.voteforlunch.model.Vote;
import ru.flint.voteforlunch.model.VoteDistribution;
import ru.flint.voteforlunch.repository.RestaurantRepository;
import ru.flint.voteforlunch.repository.UserRepository;
import ru.flint.voteforlunch.repository.VoteRepository;

import java.time.Clock;
import java.time.LocalTime;
import java.time.LocalDate;
import java.util.List;

import static ru.flint.voteforlunch.util.ValidationUtil.checkFound;

@Service
@Slf4j
public class VoteService {
    private final VoteRepository repository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final Clock clock;
    private final LocalTime timeConstraint;

    public VoteService(VoteRepository repository,
                       UserRepository userRepository,
                       RestaurantRepository restaurantRepository,
                       Clock clock,
                       @Value("${vote.time.constraint}") LocalTime timeConstraint) {

        this.repository = repository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.clock = clock;
        this.timeConstraint = timeConstraint;
    }
    public Vote get(long id, long userId) {
        log.info("Get vote with id = {}, userId = {}", id, userId);
        return checkFound(repository.getByIdAndUserId(id, userId), id, Vote.class);
    }

    public List<Vote> getAllForUser(long userId) {
        log.info("Get all votes for userId = {}", userId);
        return repository.getAllForUser(userId);
    }

    @Transactional
    @CacheEvict(key = "#result.voteDate", value = "voteDistribution")
    public Vote saveAndReturnWithDetails(long restaurantId, long userId) {
        log.info("Try to save vote. RestaurantID = {}, UserId = {}", restaurantId, userId);
        LocalDate votingDate = LocalDate.now(clock);
        Vote vote = repository.getByVoteDateAndUserId(votingDate, userId).orElseGet(() -> {
            Vote newVote = new Vote();
            newVote.setUser(userRepository.getReferenceById(userId));
            newVote.setVoteDate(votingDate);
            return newVote;
        });

        LocalTime votingTime = LocalTime.now(clock);
        if (!vote.isNew() && votingTime.isAfter(timeConstraint)) {
            throw new VoteTimeConstraintException(String.format("You can only change your vote until %s", timeConstraint));
        }

        vote.setRestaurant(restaurantRepository.getReferenceById(restaurantId));
        vote.setVoteTime(votingTime);
        vote = repository.save(vote);
        log.info("Vote saved. RestaurantID = {}, UserId = {}", restaurantId, userId);
        return vote;
    }

    @CacheEvict(value = "voteDistribution", allEntries = true)
    public void delete(long userId) {
        log.info("Try to delete vote of userId={}", userId);
        if (LocalTime.now(clock).isAfter(timeConstraint)) {
            throw new VoteTimeConstraintException(String.format("You can only change your vote until %s", timeConstraint));
        }

        LocalDate date = LocalDate.now(clock);
        if (repository.deleteByVoteDateAndUserId(date, userId) == 0) {
            throw new IllegalRequestDataException(String.format("Vote of userId = %s for date = %s not found", userId, date));
        }
        log.info("Vote deleted. userId={}", userId);
    }

    @Cacheable(key = "#date", value = "voteDistribution")
    public List<VoteDistribution> getDistributionOnDate(LocalDate date) {
        log.info("Get votes distribution on {}", date);
        return repository.getDistributionOnDate(date);
    }
}
