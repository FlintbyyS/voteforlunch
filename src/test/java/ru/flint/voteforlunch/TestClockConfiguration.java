package ru.flint.voteforlunch;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.time.Clock;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@TestConfiguration
public class TestClockConfiguration {
    @Bean
    // this custom Clock bean used to ensure that when created in the test database, records have a specific date
    public Clock testClock(@Value("${vote.time.constraint}") LocalTime timeConstraint) {
        ZonedDateTime now = ZonedDateTime.of(
                2022, 11, 15, timeConstraint.getHour() - 3, timeConstraint.getMinute(), 0, 0,
                ZoneId.of("GMT"));

        return Clock.fixed(now.toInstant(), now.getZone());
    }
}
