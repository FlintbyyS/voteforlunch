package ru.flint.voteforlunch.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@ToString(callSuper = true)
@Table(
        indexes = {
                @Index(name = "menu_restaurant_id_index",columnList = "RESTAURANT_ID")
        },
        uniqueConstraints = {
        @UniqueConstraint(name = "uc_menu_date_of_menu", columnNames = {"MENU_DATE", "RESTAURANT_ID"})
})
public class Menu extends AbstractEntity{

    @NotNull
    @Column(name = "menu_date",nullable = false)
    private LocalDate menuDate;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ToString.Exclude
    private Restaurant restaurant;

    @NotEmpty
    @OneToMany(mappedBy = "menu", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ToString.Exclude
    private Set<MenuItem> menuItemSet = new TreeSet<>();

    public void addMenuItem(MenuItem menuItem) {
        this.menuItemSet.add(menuItem);
        menuItem.setMenu(this);
    }

    public void removeMenuItem(MenuItem menuItem) {
        this.menuItemSet.remove(menuItem);
        menuItem.setMenu(null);
    }
}
