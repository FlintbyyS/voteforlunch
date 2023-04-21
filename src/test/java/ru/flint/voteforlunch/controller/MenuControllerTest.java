package ru.flint.voteforlunch.controller;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import ru.flint.voteforlunch.AbstractSpringBootTest;
import ru.flint.voteforlunch.util.exceptions.IllegalRequestDataException;
import ru.flint.voteforlunch.service.MenuService;
import ru.flint.voteforlunch.utils.LinkedHashMapMatcher;
import ru.flint.voteforlunch.utils.MatcherFactory;
import ru.flint.voteforlunch.web.dto.*;
import ru.flint.voteforlunch.web.mapper.MenuMapper;
import ru.flint.voteforlunch.web.json.JsonUtil;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.flint.voteforlunch.testdata.DishTestData.*;
import static ru.flint.voteforlunch.testdata.RestaurantTestData.AISHA_RESTAURANT;
import static ru.flint.voteforlunch.testdata.RestaurantTestData.CHERRY_RESTAURANT;
import static ru.flint.voteforlunch.testdata.UserTestData.ADMIN_MAIL;
import static ru.flint.voteforlunch.testdata.UserTestData.USER_MAIL;
import static ru.flint.voteforlunch.web.controller.MenuController.REST_URL;

public class MenuControllerTest extends AbstractSpringBootTest {
    private static final LinkedHashMapMatcher MENU_CONSTRAINTS_MATCHER = new LinkedHashMapMatcher(new LinkedHashMap<>(Map.of(
            "menuDate", "must not be null",
            "menuItems", "must not be empty")));

    private static final LinkedHashMapMatcher DISH_PRICE_CONSTRAINTS_MATCHER = new LinkedHashMapMatcher(new LinkedHashMap<>(Map.of(
            "price", "Price must be positive")));

    public static MatcherFactory.Matcher<MenuListDTO> MENU_LIST_DTO_MATCHER = MatcherFactory.usingEqualsComparator(MenuListDTO.class);
    public static MatcherFactory.Matcher<MenuResponseDTO> MENU_RESPONSE_DTO_MATCHER = MatcherFactory.usingEqualsComparator(MenuResponseDTO.class);

    public static LocalDate MINUS_TWO_DAYS = LocalDate.of(2022, 11, 13);
    public static LocalDate MINUS_ONE_DAY = LocalDate.of(2022, 11, 14);
    public static LocalDate TODAY = LocalDate.of(2022, 11, 15);

    @Autowired
    private MenuService menuService;

    @Autowired
    private MenuMapper mapper;

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getListOfMenusOnDate() throws Exception {
        mockMvc.perform(get(REST_URL + "/on-date?date=" + MINUS_TWO_DAYS))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MENU_LIST_DTO_MATCHER.contentJson(List.of(
                        new MenuListDTO(3L, MINUS_TWO_DAYS, AISHA_RESTAURANT.getId(), AISHA_RESTAURANT.getName()),
                        new MenuListDTO(1L, MINUS_TWO_DAYS, CHERRY_RESTAURANT.getId(), CHERRY_RESTAURANT.getName())
                )));
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getMenu() throws Exception {
        mockMvc.perform(get(REST_URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MENU_RESPONSE_DTO_MATCHER.contentJson(new MenuResponseDTO(1L, MINUS_TWO_DAYS, CHERRY_RESTAURANT,
                        Set.of(new MenuItemResponseDTO(1L, TEA, 10), new MenuItemResponseDTO(2L, BURGER, 15)))));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void createMenu() throws Exception {
        MenuRequestDTO menuRequestDto = new MenuRequestDTO(TODAY, CHERRY_RESTAURANT.getId(),
                Set.of(new MenuItemRequestDTO(TEA.getId(), 20), new MenuItemRequestDTO(BURGER.getId(), 30)));

        ResultActions resultActions = mockMvc.perform(post(REST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.writeValue(menuRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        MenuResponseDTO menuResponseDto = MENU_RESPONSE_DTO_MATCHER.readFromJson(resultActions);

        assertThat(menuResponseDto.getId()).isNotNull();
        assertThat(menuResponseDto.getMenuDate()).isEqualTo(TODAY);
        assertThat(menuResponseDto.getMenuItems().size()).isEqualTo(2);
        assertThat(menuResponseDto.getMenuItems())
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(Set.of(
                        new MenuItemResponseDTO(null, TEA, 20),
                        new MenuItemResponseDTO(null, BURGER, 30)
                ));
        assertThat(menuResponseDto).isEqualTo(mapper.toDTO(menuService.getByIdWithAllData(menuResponseDto.getId())));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateMenu() throws Exception {
        MenuRequestDTO updatedMenuRequestDto = new MenuRequestDTO(TODAY.plusDays(1), AISHA_RESTAURANT.getId(),
                Set.of(new MenuItemRequestDTO(SOUP.getId(), 15), new MenuItemRequestDTO(SANDWICH.getId(), 25)));

        ResultActions resultActions = mockMvc.perform(put(REST_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.writeValue(updatedMenuRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        MenuResponseDTO menuResponseDto = MENU_RESPONSE_DTO_MATCHER.readFromJson(resultActions);

        assertThat(menuResponseDto.getId()).isEqualTo(1L);
        assertThat(menuResponseDto.getMenuDate()).isEqualTo(TODAY.plusDays(1));
        assertThat(menuResponseDto.getMenuItems().size()).isEqualTo(2);
        assertThat(menuResponseDto.getMenuItems())
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(Set.of(
                        new MenuItemResponseDTO(null, SOUP, 15),
                        new MenuItemResponseDTO(null, SANDWICH, 25)
                ));
        assertThat(menuResponseDto).isEqualTo(mapper.toDTO(menuService.getByIdWithAllData(1L)));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void deleteMenu() throws Exception {
        mockMvc.perform(delete(REST_URL + "/2")).andExpect(status().isNoContent());

        assertThrows(IllegalRequestDataException.class, () -> menuService.getByIdWithAllData(2L));
    }

    @Nested
    class ErrorCasesWithMenu {
        @Test
        void getUnauthorized() throws Exception {
            mockMvc.perform(get(REST_URL + "/1"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithUserDetails(value = USER_MAIL)
        void getForbidden() throws Exception {
            mockMvc.perform(delete(REST_URL + "/1")).andExpect(status().isForbidden());
        }

        @Test
        @WithUserDetails(value = ADMIN_MAIL)
        void createUnprocessableMenu() throws Exception {
            MenuRequestDTO menuRequestDto = new MenuRequestDTO(null, -10, null);

            mockMvc.perform(post(REST_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonUtil.writeValue(menuRequestDto)))
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.invalid_params").value(MENU_CONSTRAINTS_MATCHER));
        }

        @Test
        @WithUserDetails(value = ADMIN_MAIL)
        void updateUnprocessableMenu() throws Exception {
            MenuRequestDTO menuRequestDto = new MenuRequestDTO(null, -10, null);

            mockMvc.perform(put(REST_URL + "/3")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonUtil.writeValue(menuRequestDto)))
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.invalid_params").value(MENU_CONSTRAINTS_MATCHER));
        }

        @Test
        @WithUserDetails(value = ADMIN_MAIL)
        void createDuplicateMenu() throws Exception {
            mockMvc.perform(post(REST_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonUtil.writeValue(new MenuRequestDTO(
                                    MINUS_ONE_DAY,
                                    AISHA_RESTAURANT.getId(),
                                    Set.of(new MenuItemRequestDTO(TEA.getId(), 10))))))
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.detail").value("Menu for this restaurant on this date already exists"));
        }

        @Test
        @WithUserDetails(value = ADMIN_MAIL)
        void updateDuplicateMenu() throws Exception {
            mockMvc.perform(put(REST_URL + "/3")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonUtil.writeValue(new MenuRequestDTO(
                                    MINUS_ONE_DAY,
                                    AISHA_RESTAURANT.getId(),
                                    Set.of(new MenuItemRequestDTO(TEA.getId(), 10))))))
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.detail").value("Menu for this restaurant on this date already exists"));
        }

        @Test
        @WithUserDetails(value = ADMIN_MAIL)
        void restaurantNotExist() throws Exception {
            mockMvc.perform(post(REST_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonUtil.writeValue(new MenuRequestDTO(
                                    MINUS_ONE_DAY,
                                    100,
                                    Set.of(new MenuItemRequestDTO(TEA.getId(), 10))))))
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.detail").value("Wrong id for restaurant"));
        }

        @Test
        @WithUserDetails(value = ADMIN_MAIL)
        void dishNotExist() throws Exception {
            mockMvc.perform(post(REST_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonUtil.writeValue(new MenuRequestDTO(
                                    MINUS_ONE_DAY,
                                    CHERRY_RESTAURANT.getId(),
                                    Set.of(new MenuItemRequestDTO(100, 10))))))
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.detail").value("Unable to find ru.flint.voteforlunch.model.Dish with id 100"));
        }

        @Test
        @WithUserDetails(value = ADMIN_MAIL)
        void wrongMenuItem() throws Exception {
            Locale.setDefault(Locale.ENGLISH);
            mockMvc.perform(post(REST_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonUtil.writeValue(new MenuRequestDTO(
                                    TODAY,
                                    AISHA_RESTAURANT.getId(),
                                    Set.of(new MenuItemRequestDTO(TEA.getId(), 0))))))
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.invalid_params").value(DISH_PRICE_CONSTRAINTS_MATCHER));
        }
    }
}