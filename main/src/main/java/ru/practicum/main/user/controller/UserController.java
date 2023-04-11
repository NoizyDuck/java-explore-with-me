package ru.practicum.main.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.user.dto.CreateUserDto;
import ru.practicum.main.user.dto.UserDto;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/admin/users")
    public List<User> getAllUsersByIds(@RequestParam List<Long> ids,
                                       @RequestParam(defaultValue = "0", required = false) Integer from,
                                       @RequestParam(defaultValue = "10", required = false) Integer size) {
        return userService.getAllUsersByIds(ids, from, size);
    }

    @PostMapping("/admin/users")
    @ResponseStatus(value = HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody CreateUserDto createUserDto) {
        return userService.createUser(createUserDto);
    }

    @DeleteMapping("/admin/users/{userId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable Long userId) {
        userService.deleteUserById(userId);
    }

}
