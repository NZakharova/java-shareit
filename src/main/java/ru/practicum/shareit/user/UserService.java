package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.utils.DuplicateObjectException;
import ru.practicum.shareit.utils.ObjectNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserValidator userValidator;
    private final UserDtoValidator userDtoValidator;
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository, UserValidator userValidator, UserDtoValidator userDtoValidator) {
        this.userRepository = userRepository;
        this.userValidator = userValidator;
        this.userDtoValidator = userDtoValidator;
    }

    public int add(UserDto user) {
        var userModel = convertAndValidate(user);

        // если email дублируется, то база данных сначал увеличивает счётчик, а потом кидает исключение.
        // из-за этого созданный пользователь имеет неправильный id и тесты не проходят
        if (userRepository.findByEmail(userModel.getEmail()) != null) {
            throw new DuplicateObjectException("email");
        }

        return userRepository.save(userModel).getId();
    }

    public UserDto find(int id) {
        return UserMapper.toDto(userRepository.findById(id).orElseThrow(() -> { throw new ObjectNotFoundException(id); }));
    }

    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(UserMapper::toDto).collect(Collectors.toList());
    }

    public void update(UserDto dto) {
        userDtoValidator.validateForUpdate(dto);

        var existing = userRepository.findById(dto.getId()).orElseThrow(() -> { throw new ObjectNotFoundException(dto.getId()); });

        if (dto.getEmail() != null) {
            existing.setEmail(dto.getEmail());
        }

        if (dto.getName() != null) {
            existing.setName(dto.getName());
        }

        userRepository.save(existing);
    }

    public void delete(int id) {
        userRepository.deleteById(id);
    }

    private User convertAndValidate(UserDto dto) {
        var model = UserMapper.toModel(dto);
        userValidator.validate(model);
        return model;
    }
}
