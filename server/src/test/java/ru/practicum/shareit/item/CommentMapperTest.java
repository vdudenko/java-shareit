package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentInfoDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CommentMapperTest {

    @Test
    void toCommentShouldConvertDtoToComment() {
        CommentDto dto = new CommentDto("Отличная вещь!");

        Comment comment = CommentMapper.toComment(dto);

        assertThat(comment.getText()).isEqualTo("Отличная вещь!");
        assertThat(comment.getCreated()).isNotNull();
        assertThat(comment.getId()).isNull();
        assertThat(comment.getAuthor()).isNull();
        assertThat(comment.getItem()).isNull();
    }

    @Test
    void commentInfoDtoShouldConvertCommentToDto() {
        Long commentId = 100L;
        LocalDateTime now = LocalDateTime.now();

        User author = new User(1L, "vadim@example.com", "Vadim");
        Comment comment = Comment.builder()
                .id(commentId)
                .text("Супер!")
                .author(author)
                .created(now)
                .build();

        CommentInfoDto dto = CommentMapper.commentInfoDto(comment);

        assertThat(dto.getId()).isEqualTo(commentId);
        assertThat(dto.getText()).isEqualTo("Супер!");
        assertThat(dto.getAuthorName()).isEqualTo("Vadim");
        assertThat(dto.getCreated()).isEqualTo(now);
    }

    @Test
    void commentInfoDtoShouldHandleNullAuthor() {
        Comment comment = Comment.builder()
                .id(1L)
                .text("Текст")
                .author(null)
                .created(LocalDateTime.now())
                .build();

        assertThatThrownBy(() -> CommentMapper.commentInfoDto(comment))
                .isInstanceOf(NullPointerException.class);
    }
}