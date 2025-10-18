package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.utils.HeaderConstants.USER_ID_HEADER;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    void create() throws Exception {
        Long userId = 1L;

        ItemRequestDto itemRequestDto = new ItemRequestDto("desc", userId);

        User user = User.builder()
                .id(userId)
                .name("Vadim")
                .email("dudenko.vadim@google.com")
                .build();

        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("desc")
                .requestor(user)
                .build();

        Mockito.when(itemRequestService.create(Mockito.any(), Mockito.any())).thenReturn(itemRequest);

        String result = mockMvc.perform(post("/requests")
                        .header(USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequest), result);
    }

    @Test
    void getAllByRequester() throws Exception {
        Integer from = 0;
        Integer size = 20;
        Long userId = 1L;

        ItemRequestWithItemsDto itemRequest = ItemRequestWithItemsDto.builder()
                .id(1L)
                .description("desc")
                .requesterId(userId)
                .items(List.of())
                .build();

        Mockito.when(itemRequestService.getAllByRequester(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(itemRequest));

        String result = mockMvc.perform(get("/requests")
                        .header(USER_ID_HEADER, userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(itemRequest)), result);
    }

    @Test
    void getAll() throws Exception {
        Integer from = 0;
        Integer size = 20;
        Long userId = 1L;

        ItemRequestWithItemsDto itemRequest = ItemRequestWithItemsDto.builder()
                .id(1L)
                .description("desc")
                .requesterId(userId)
                .items(List.of())
                .build();

        Mockito.when(itemRequestService.getAll(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(itemRequest));

        String result = mockMvc.perform(get("/requests/all")
                        .header(USER_ID_HEADER, userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(itemRequest)), result);
    }

    @Test
    void getById() throws Exception {
        Long userId = 1L;
        Long requestId = 1L;

        ItemRequestWithItemsDto itemRequest = ItemRequestWithItemsDto.builder()
                .id(1L)
                .description("description")
                .requesterId(userId)
                .build();

        Mockito.when(itemRequestService.getById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(itemRequest);

        String result = mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header(USER_ID_HEADER, 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequest), result);
    }
}
