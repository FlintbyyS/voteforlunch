package ru.flint.voteforlunch.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.flint.voteforlunch.model.*;
import ru.flint.voteforlunch.repository.*;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Component
public class InitializeData implements CommandLineRunner {
    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final MenuRepository menuRepository;
    private final RestaurantRepository restaurantRepository;
    private final DishRepository dishRepository;
    private final Clock clock;
    public InitializeData(VoteRepository voteRepository, UserRepository userRepository, MenuRepository menuRepository,
                          RestaurantRepository restaurantRepository, DishRepository dishRepository, Clock clock) {
        this.voteRepository = voteRepository;
        this.userRepository = userRepository;
        this.menuRepository = menuRepository;
        this.restaurantRepository = restaurantRepository;
        this.dishRepository = dishRepository;
        this.clock = clock;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        Dish tea = new Dish("Tea");
        Dish burger = new Dish("Burger");
        Dish soup = new Dish("Soup");
        Dish pasta = new Dish("Pasta");
        Dish sandwich = new Dish("Sandwich");
        dishRepository.saveAll(List.of(tea, burger, soup, pasta, sandwich));

        Restaurant cherryRestaurant = new Restaurant("Cherry");
        Restaurant aishaRestaurant = new Restaurant("Aisha");
        restaurantRepository.saveAll(List.of(cherryRestaurant, aishaRestaurant));

        // pre-defined users for testing purposes
        User admin = new User("admin@ya.ru", "admin", "admin", "{noop}admin", true, Collections.singleton(Role.ADMIN));
        User user = new User("user@ya.ru", "user", "user", "{noop}user", true, Collections.singleton(Role.USER));
        // collection of users to create large amount of votes for run-time testing using curl
        List<User> userList = new ArrayList<>(2002);
        userList.add(admin);
        userList.add(user);
        for (int i = 0; i < 2000; i++) {
            userList.add(new User("user" + i + "@gmail.com", "userName" + i, "userSurname" + i, "{noop}user" + i, true, Collections.singleton(Role.USER)));
        }
        userRepository.saveAll(userList);

        // create two menus
        LocalDate nowMinusTwoDays = LocalDate.now(clock).minusDays(2);
        LocalDate nowMinusOneDay = LocalDate.now(clock).minusDays(1);

        Menu menuForCherry1 = new Menu();
        menuForCherry1.setMenuDate(nowMinusTwoDays);
        menuForCherry1.setRestaurant(cherryRestaurant);
        menuForCherry1.setMenuItemSet(new TreeSet<>(Set.of(
                new MenuItem(tea, menuForCherry1, 10),
                new MenuItem(burger,menuForCherry1,15)
        )));
        Menu menuForCherry2 = new Menu();
        menuForCherry2.setMenuDate(nowMinusOneDay);
        menuForCherry2.setRestaurant(cherryRestaurant);
        menuForCherry2.setMenuItemSet(new TreeSet<>(Set.of(
                new MenuItem(soup, menuForCherry2, 25),
                new MenuItem(burger, menuForCherry2, 15)
        )));
        Menu menuForAisha1 = new Menu();
        menuForAisha1.setMenuDate(nowMinusTwoDays);
        menuForAisha1.setRestaurant(aishaRestaurant);
        menuForAisha1.setMenuItemSet(new TreeSet<>(Set.of(
                new MenuItem(tea, menuForAisha1, 15),
                new MenuItem(pasta, menuForAisha1, 25)
        )));
        Menu menuForAisha2 = new Menu();
        menuForAisha2.setMenuDate(nowMinusOneDay);
        menuForAisha2.setRestaurant(aishaRestaurant);
        menuForAisha2.setMenuItemSet(new TreeSet<>(Set.of(
                new MenuItem(sandwich, menuForAisha2, 25),
                new MenuItem(tea, menuForAisha2, 15)
        )));
        menuRepository.saveAll(List.of(menuForCherry1, menuForCherry2, menuForAisha1, menuForAisha2));

        List<Vote> votes = new ArrayList<>();
        // this votes will be used for testing
        votes.add(new Vote(admin, aishaRestaurant, nowMinusTwoDays, LocalTime.of(9, 30)));
        votes.add(new Vote(user, aishaRestaurant, nowMinusTwoDays, LocalTime.of(10, 30)));
        votes.add(new Vote(admin, cherryRestaurant, nowMinusOneDay, LocalTime.of(9, 30)));
        votes.add(new Vote(user, aishaRestaurant, nowMinusOneDay, LocalTime.of(10, 30)));
        // and this votes can be used for testing in run-time using curl
        Random rnd = new Random();
        userList.stream().skip(2L).forEach(usr -> votes.add(new Vote(
                usr,
                rnd.nextBoolean() ? aishaRestaurant : cherryRestaurant,
                LocalDate.now(clock),
                LocalTime.of(rnd.nextInt(6, 11), rnd.nextInt(0, 60)))));
        voteRepository.saveAll(votes);
    }
}
