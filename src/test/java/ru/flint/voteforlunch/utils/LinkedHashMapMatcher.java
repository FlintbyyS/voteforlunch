package ru.flint.voteforlunch.utils;

import jakarta.validation.constraints.NotNull;
import org.assertj.core.matcher.AssertionMatcher;

import java.util.LinkedHashMap;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class LinkedHashMapMatcher extends AssertionMatcher<LinkedHashMap<String, String>> {
    private final LinkedHashMap<String, String> expectedHashMap;

    public LinkedHashMapMatcher(@NotNull LinkedHashMap<String, String> expectedHashMap) {
        this.expectedHashMap = expectedHashMap;
    }

    @Override
    public void assertion(LinkedHashMap<String, String> actualHashMap) throws AssertionError {
        assertThat(actualHashMap).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(expectedHashMap);
    }
}
