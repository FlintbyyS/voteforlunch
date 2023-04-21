package ru.flint.voteforlunch.controller;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import ru.flint.voteforlunch.AbstractSpringBootTest;
import ru.flint.voteforlunch.model.VoteDistribution;
import ru.flint.voteforlunch.utils.MatcherFactory;
import ru.flint.voteforlunch.web.dto.VoteDTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.flint.voteforlunch.testdata.RestaurantTestData.AISHA_RESTAURANT;
import static ru.flint.voteforlunch.testdata.RestaurantTestData.CHERRY_RESTAURANT;
import static ru.flint.voteforlunch.testdata.UserTestData.ADMIN_MAIL;
import static ru.flint.voteforlunch.web.controller.VoteController.REST_URL;

public class VoteControllerTest extends AbstractSpringBootTest {
    public static MatcherFactory.Matcher<VoteDTO> VOTE_DTO_MATCHER = MatcherFactory.usingEqualsComparator(VoteDTO.class);
    public static MatcherFactory.Matcher<VoteDistribution> VOTE_DISTRIBUTION_MATCHER = MatcherFactory.usingEqualsComparator(VoteDistribution.class);

    public static VoteDTO VOTE_AISHA_1L = new VoteDTO(1L, AISHA_RESTAURANT, LocalDate.of(2022, 11, 13), LocalTime.of(9, 30));
    public static VoteDTO VOTE_CHERRY_3L = new VoteDTO(3L, CHERRY_RESTAURANT, LocalDate.of(2022, 11, 14), LocalTime.of(9, 30));

    public static List<VoteDTO> VOTES = List.of(VOTE_CHERRY_3L, VOTE_AISHA_1L);

    public static List<VoteDistribution> VOTE_DISTRIBUTION = List.of(
            new VoteDistribution(2L, "Aisha", 2L),
            new VoteDistribution(1L, "Cherry", 0L)
    );

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getVotesForUser() throws Exception {
        mockMvc.perform(get(REST_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(VOTE_DTO_MATCHER.contentJson(VOTES));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getVote() throws Exception {
        mockMvc.perform(get(REST_URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(VOTE_DTO_MATCHER.contentJson(VOTE_AISHA_1L));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getVotesDistribution() throws Exception {
        mockMvc.perform(get(REST_URL +
                        "/distribution?date=" +
                        LocalDate.now(clock).minusDays(2).format(DateTimeFormatter.ISO_DATE)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(VOTE_DISTRIBUTION_MATCHER.contentJson(VOTE_DISTRIBUTION));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void saveVote() throws Exception {
        ResultActions resultActions = mockMvc.perform(post(REST_URL).param("restaurantId", "1"))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        VoteDTO actual = VOTE_DTO_MATCHER.readFromJson(resultActions);

        assertThat(actual.getVoteDate()).isEqualTo(LocalDate.now(clock));
        assertThat(actual.getRestaurant().getId()).isEqualTo(1L);
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void deleteVote() throws Exception {
        mockMvc.perform(post(REST_URL).param("restaurantId", "1"));

        mockMvc.perform(delete(REST_URL)).andExpect(status().isNoContent());
    }

    @Nested
    class ErrorCasesForVote {
        @Test
        void getUnauthorized() throws Exception {
            mockMvc.perform(get(REST_URL + "/1"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithUserDetails(value = ADMIN_MAIL)
        void voteNotFound() throws Exception {
            mockMvc.perform(delete(REST_URL))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.detail").value("Vote of userId = 1 for date = 2022-11-15 not found"));
        }
    }
}