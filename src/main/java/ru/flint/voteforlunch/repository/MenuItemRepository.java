package ru.flint.voteforlunch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.flint.voteforlunch.model.MenuItem;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem,Long> {
}
