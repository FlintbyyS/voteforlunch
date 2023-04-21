package ru.flint.voteforlunch.util;

import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.lang.NonNull;
import ru.flint.voteforlunch.web.dto.AbstractDTO;
import ru.flint.voteforlunch.util.exceptions.IllegalRequestDataException;

import java.util.Optional;

@UtilityClass
public class ValidationUtil {
    public static void checkNew(@NotNull AbstractDTO bean) {
        if (!bean.isNew()) {
            throw new IllegalRequestDataException(bean.getClass().getSimpleName() + " must be new (id=null)");
        }
    }
    @NonNull
    public static Throwable getRootCause(@NonNull Throwable t) {
        Throwable rootCause = NestedExceptionUtils.getRootCause(t);
        return rootCause != null ? rootCause : t;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T> T checkFound(@NonNull Optional<T> obj, long id, Class<T> clazz) {
        if (obj.isEmpty()) {
            throw new IllegalRequestDataException(String.format("%s with id = %d not found", clazz.getSimpleName(), id));
        }
        return obj.get();
    }

    public static <T> void checkExist(boolean exist, long id, Class<T> clazz) {
        if (!exist) {
            throw new IllegalRequestDataException(String.format("%s with id = %d not found", clazz.getSimpleName(), id));
        }
    }
}
