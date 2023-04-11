package ru.practicum.main.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.main.user.dto.CreateUserDto;
import ru.practicum.main.user.dto.UserDto;
import ru.practicum.main.user.model.User;

import java.util.List;

@Service
public interface UserService {
    List<User> getAllUsersByIds(List<Long> ids, Integer from, Integer size);

    UserDto createUser(CreateUserDto createUserDto);

    void deleteUserById(Long id);
}
