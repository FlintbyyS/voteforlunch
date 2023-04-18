package ru.flint.voteforlunch.repository;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.flint.voteforlunch.model.Menu;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu,Long> {
    @EntityGraph(attributePaths = "restaurant")
    @Query("select m from Menu m where m.menuDate = :date order by m.menuDate desc, m.restaurant.name asc")
    List<Menu> findAllWithRestaurantsOnDate(@NotNull @Param("date") LocalDate date);
    @EntityGraph(attributePaths = {"restaurant","menuItemSet","menuItemSet.dish"})
    @Query("select m from Menu m where m.id = :id")
    Optional<Menu> findAllByIdWithAllData(@Param("id") Long id);
}
