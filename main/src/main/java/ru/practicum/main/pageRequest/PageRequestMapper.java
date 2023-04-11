package ru.practicum.main.pageRequest;

import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import ru.practicum.main.exceptions.IncorrectParameterException;

@NoArgsConstructor
public class PageRequestMapper {

    public static PageRequest pageRequestValidaCreate(Integer from, Integer size) {
        if (from == null || size == null) {
            return null;
        }
        if (size <= 0 || from < 0) {
            throw new IncorrectParameterException("Incorrect from or size");
        }
        int page = from / size;
        return PageRequest.of(page, size);
    }
}
