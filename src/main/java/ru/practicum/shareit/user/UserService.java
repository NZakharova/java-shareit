package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;
    private final UserValidator userValidator;
    private final UserDtoValidator userDtoValidator;

    public UserService(UserStorage userStorage, UserValidator userValidator, UserDtoValidator userDtoValidator) {
        this.userStorage = userStorage;
        this.userValidator = userValidator;
        this.userDtoValidator = userDtoValidator;
    }

    public int add(UserDto user) {
        userDtoValidator.validateForAdd(user);
        return userStorage.add(convertAndValidate(user));
    }

    public UserDto find(int id) {
        return UserMapper.toDto(userStorage.find(id));
    }

    public List<UserDto> findAll() {
        return userStorage.findAll().stream().map(UserMapper::toDto).collect(Collectors.toList());
    }

    public void update(UserDto dto) {
        userDtoValidator.validateForUpdate(dto);

        if (dto.getEmail() != null) {
            userStorage.updateEmail(dto.getId(), dto.getEmail());
        }

        if (dto.getName() != null) {
            userStorage.updateName(dto.getId(), dto.getName());
        }
    }

    public void delete(int id) {
        userStorage.delete(id);
    }

    private User convertAndValidate(UserDto dto) {
        var model = UserMapper.toModel(dto);
        userValidator.validate(model);
        return model;
    }

}
