package ru.flint.voteforlunch.model;

public record VoteDistribution (
        Long restaurantId,
        String restaurantName,
        Long voteCount) {
    public VoteDistribution(Long restaurantId, String restaurantName, Long voteCount) {
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.voteCount = voteCount == null ? 0 : voteCount;
    }
}
