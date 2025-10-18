package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import java.util.List;
import static ru.practicum.shareit.utils.HeaderConstants.USER_ID_HEADER;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemInfoDto> get(@RequestHeader(USER_ID_HEADER) long userId) {
        return itemService.getItems(userId);
    }

    @GetMapping("/{itemId}")
    public ItemInfoDto getItem(@RequestHeader(USER_ID_HEADER) Long userId, @PathVariable Long itemId) {
        return itemService.getItem(userId, itemId);
    }

    @GetMapping("/search")
    public List<Item> getItem(@RequestParam String text) {
        return itemService.getItemsByText(text);
    }

    @PostMapping
    public Item add(@RequestHeader(USER_ID_HEADER) Long userId, @RequestBody ItemCreateDto item) {
        return itemService.addNewItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public Item update(@RequestHeader(USER_ID_HEADER) Long userId, @PathVariable Long itemId, @Valid @RequestBody ItemDto item) {
        return itemService.updateItem(userId, itemId, item);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(USER_ID_HEADER) Long userId, @PathVariable Long itemId) {
        itemService.deleteItem(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentInfoDto addComment(@RequestHeader(USER_ID_HEADER) Long userId, @PathVariable Long itemId, @Valid @RequestBody CommentDto commentDto) {
        return itemService.createComment(userId, itemId, commentDto);
    }
}
