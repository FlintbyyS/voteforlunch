package ru.flint.voteforlunch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@SpringBootTest()
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ContextConfiguration(classes = {TestClockConfiguration.class})
public abstract class AbstractSpringBootTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected Clock clock;
}
