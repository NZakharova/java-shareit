package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserValidator userValidator;
    private final UserDtoValidator userDtoValidator;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public int add(UserDto user) {
        var userModel = convertAndValidate(user);

        return userRepository.save(userModel).getId();
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto get(int id) {
        return UserMapper.toDto(userRepository.findById(id).orElseThrow());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAll() {
        return userRepository.findAll().stream().map(UserMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
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
    @Transactional
    public void delete(int id) {
        userRepository.deleteById(id);
    }

    private User convertAndValidate(UserDto dto) {
        var model = UserMapper.toModel(dto);
        userValidator.validate(model);
        return model;
    }
}
