package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserValidator userValidator;
    private final UserDtoValidator userDtoValidator;
    private final UserRepository userRepository;

    @Override
    public int add(UserDto user) {
        var userModel = convertAndValidate(user);

        return userRepository.save(userModel).getId();
    }

    @Override
    public UserDto get(int id) {
        return UserMapper.toDto(userRepository.findById(id).orElseThrow());
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream().map(UserMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public void update(UserDto dto) {
        userDtoValidator.validateForUpdate(dto);

        var existing = userRepository.findById(dto.getId()).orElseThrow();

        if (dto.getEmail() != null) {
            existing.setEmail(dto.getEmail());
        }

        if (dto.getName() != null) {
            existing.setName(dto.getName());
        }

        userRepository.save(existing);
    }

    @Override
    public void delete(int id) {
        userRepository.deleteById(id);
    }

    private User convertAndValidate(UserDto dto) {
        var model = UserMapper.toModel(dto);
        userValidator.validate(model);
        return model;
    }
}
