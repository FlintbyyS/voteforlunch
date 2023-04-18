package ru.flint.voteforlunch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.flint.voteforlunch.model.Dish;

@Repository
public interface DishRepository extends JpaRepository<Dish,Long> {
}
