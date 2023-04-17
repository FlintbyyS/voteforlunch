package ru.flint.voteforlunch.dto;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Data
/*
 * Mind that you have to add hand-made AllArgsConstructor to all inheritors that use @Value
 * because otherwise you can get deserialization error "Cannot construct instance of..."
 */
public class AbstractDTO {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    protected Long id;

    @Hidden
    public boolean isNew() {
        return id == null;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + id;
    }
}