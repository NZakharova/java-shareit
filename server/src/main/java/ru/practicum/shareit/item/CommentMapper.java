package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentMapper {
    private final UserService userService;

    public CommentDto toDto(Comment comment) {
        return CommentDto
                .builder()
                .id(comment.getId())
                .authorName(userService.get(comment.getUserId()).getName())
                .text(comment.getText())
                .created(comment.getTime())
                .build();
    }

    public Comment toModel(CommentDto comment, int itemId, int authorId, LocalDateTime time) {
        return new Comment(
                Optional.ofNullable(comment.getId()).orElse(0),
                comment.getText(),
                authorId,
                itemId,
                time
        );
    }
}
