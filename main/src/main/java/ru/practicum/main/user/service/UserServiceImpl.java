package ru.practicum.main.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.exceptions.NotFoundException;
import ru.practicum.main.exceptions.UserParamException;
import ru.practicum.main.pageRequest.PageRequestMapper;
import ru.practicum.main.user.dto.CreateUserDto;
import ru.practicum.main.user.dto.UserDto;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.repository.UserRepository;
import ru.practicum.main.user.userMapper.UserMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<User> getAllUsersByIds(List<Long> ids, Integer from, Integer size) {
        PageRequest pageRequest = PageRequestMapper.pageRequestValidaCreate(from, size);

        return userRepository.findAllByIdIn(ids, pageRequest);
    }

    @Transactional
    @Override
    public UserDto createUser(CreateUserDto createUserDto) {
        User user = userMapper.createUserToUser(createUserDto);
        if (userRepository.existsByName(user.getName())) {
            throw new UserParamException("user with this name already exist");
        }
        return userMapper.userToUserDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUserById(Long id) {
        userRepository.findById(id).orElseThrow(() ->
                new NotFoundException("User id " + id + " not found"));
        userRepository.deleteById(id);
    }
}
