package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoTest {
    @Autowired
    private JacksonTester<ItemRequestDto> itemRequestDtoJson;

    @Autowired
    private JacksonTester<ItemRequestWithItemsDto> itemRequestWithItemsDtoJson;

    @Test
    void testItemRequestDto() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto("desc", 1L);

        JsonContent<ItemRequestDto> result = itemRequestDtoJson.write(itemRequestDto);

        assertThat(result).hasJsonPath("$.description");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemRequestDto.getDescription());

        assertThat(result).hasJsonPath("$.requesterId");
        assertThat(result).extractingJsonPathNumberValue("$.requesterId")
                .isEqualTo(itemRequestDto.getRequesterId().intValue());

        ItemRequestDto itemRequestDtoForTest = itemRequestDtoJson.parseObject(result.getJson());

        assertThat(itemRequestDtoForTest).isEqualTo(itemRequestDto);
    }

    @Test
    void testItemRequestWithItemsDto() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        ItemRequestWithItemsDto itemRequestWithItemsDto = ItemRequestWithItemsDto.builder()
                .id(1L)
                .description("desc")
                .requesterId(2L)
                .created(now)
                .build();

        JsonContent<ItemRequestWithItemsDto> result = itemRequestWithItemsDtoJson.write(itemRequestWithItemsDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(itemRequestWithItemsDto.getId().intValue());

        assertThat(result).hasJsonPath("$.description");
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemRequestWithItemsDto.getDescription());

        assertThat(result).hasJsonPath("$.requesterId");
        assertThat(result).extractingJsonPathNumberValue("$.requesterId")
                .isEqualTo(itemRequestWithItemsDto.getRequesterId().intValue());

        assertThat(result).hasJsonPath("$.created");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo(itemRequestWithItemsDto.getCreated().toString());

        ItemRequestWithItemsDto parsed = itemRequestWithItemsDtoJson.parseObject(result.getJson());
        assertThat(parsed).isEqualTo(itemRequestWithItemsDto);
    }
}
