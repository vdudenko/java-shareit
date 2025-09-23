package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<Item> get(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getItems(userId);
    }

    @GetMapping("/{itemId}")
    public Optional<Item> getItem(@PathVariable Long itemId) {
        return itemService.getItem(itemId);
    }

    @GetMapping("/search")
    public List<Item> getItem(@RequestParam String text) {
        return itemService.getItemsByText(text);
    }

    @PostMapping
    public Item add(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody Item item) {
        return itemService.addNewItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public Item update(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId, @Valid @RequestBody ItemDto item) {
        return itemService.updateItem(userId, itemId, item);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable long itemId) {
        itemService.deleteItem(userId, itemId);
    }
}
