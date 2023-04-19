package ru.flint.voteforlunch.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.flint.voteforlunch.model.Vote;
import ru.flint.voteforlunch.model.VoteDistribution;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface VoteRepository extends JpaRepository<Vote,Long> {
    @Query("select v from Vote v where v.user.id = :userId order by v.voteDate desc, v.voteTime desc")
    @EntityGraph(attributePaths = {"restaurant"})
    List<Vote> getAllForUser(@Param("userId") long userId);

    @EntityGraph(attributePaths = {"restaurant"})
    Optional<Vote> getByIdAndUserId(long id, long userId);

    Optional<Vote> getByVoteDateAndUserId(LocalDate date, long userId);

    @Query(name = "get_votes_distribution_on_date_for_all_restaurants", nativeQuery = true)
    List<VoteDistribution> getDistributionOnDate(@Param("distDate") LocalDate distDate);

    long deleteByVoteDateAndUserId(LocalDate voteDate, long userId);
}
