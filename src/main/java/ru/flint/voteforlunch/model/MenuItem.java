package ru.flint.voteforlunch.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(
        indexes = {
        @Index(name = "menu_item_dish_id_index",columnList = "DISH_ID"),
        @Index(name = "menu_item_menu_id_index",columnList = "MENU_ID")
})
public class MenuItem extends AbstractEntity implements Comparable<MenuItem>{

    @ManyToOne
    @JoinColumn(name = "dish_id",nullable = false)
    private Dish dish;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ToString.Exclude
    private Menu menu;
    @NotNull
    @Positive
    @Column(name = "price",nullable = false)
    private Integer price;

    @Override
    public int compareTo(MenuItem comparedItem) {
        if (dish == null || comparedItem.getDish() == null) {
            return 0;
        }
        return comparedItem.getDish().getName().compareTo(dish.getName());
    }
}
