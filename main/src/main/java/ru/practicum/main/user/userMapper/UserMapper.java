package ru.practicum.main.user.userMapper;

import org.springframework.stereotype.Component;
import ru.practicum.main.user.dto.CreateUserDto;
import ru.practicum.main.user.dto.UserDto;
import ru.practicum.main.user.dto.UserShortDto;
import ru.practicum.main.user.model.User;

@Component
public class UserMapper {
    public UserShortDto userToShortDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    public User createUserToUser(CreateUserDto createUserDto) {
        return User.builder()
                .email(createUserDto.getEmail())
                .name(createUserDto.getName())
                .build();
    }

    public UserDto userToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }
}
