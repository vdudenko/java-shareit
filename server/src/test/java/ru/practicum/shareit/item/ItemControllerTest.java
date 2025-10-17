package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    private ItemCreateDto itemCreateDto;

    private Item item;

    private ItemInfoDto itemInfoDto;

    private CommentInfoDto commentInfoDto;

    @BeforeEach
    void create() {
        itemCreateDto = ItemCreateDto.builder()
                .name("name")
                .description("desc")
                .available(true)
                .requestId(1L)
                .build();

        item = Item.builder()
                .id(1L)
                .description("desc")
                .available(true)
                .build();

        itemInfoDto = ItemInfoDto.builder()
                .id(1L)
                .name("test")
                .build();

        commentInfoDto = CommentInfoDto.builder()
                .id(1L)
                .text("test")
                .build();
    }

    @Test
    void getAll() throws Exception {
        long userId = 1L;

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(itemService).getItems(userId);
    }

    @Test
    void getItem() throws Exception {
        Long itemId = 1L;
        Long userId = 1L;
        Mockito.when(itemService.getItem(Mockito.anyLong(), Mockito.anyLong())).thenReturn(itemInfoDto);

        String result = mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemInfoDto), result);
        Mockito.verify(itemService).getItem(userId, itemId);
    }

    @Test
    void getItemSearch() throws Exception {
        String text = "";

        String result = mockMvc.perform(get("/items/search")
                        .param("text", "")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals("[]", result);
    }

    @Test
    void add() throws Exception {
        Mockito.when(itemService.addNewItem(Mockito.any(), Mockito.any())).thenReturn(item);

        String result = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemCreateDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(item), result);
    }

    @Test
    void update() throws Exception {
        Long itemId = 1L;
        ItemDto itemDto = ItemDto.builder().name("test2").build();

        Mockito.when(itemService.updateItem(Mockito.anyLong(), Mockito.eq(itemId), Mockito.any()))
                .thenReturn(item);

        String result = mockMvc.perform(patch("/items/{itemId}", itemId) // ← itemId, а не itemDto!
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(item), result);
    }

    @Test
    void deleteItem() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        mockMvc.perform(delete("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk());

        Mockito.verify(itemService).deleteItem(userId, itemId);
    }

    @Test
    void addComment() throws Exception {
        Long id = 1L;

        Mockito.when(itemService.createComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.any()))
                .thenReturn(commentInfoDto);

        String result = mockMvc.perform(post("/items/{id}/comment", id)
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new CommentDto("test"))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(commentInfoDto), result);
    }

}
