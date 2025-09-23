package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import java.util.*;

@Repository
public class InMemoryItemRepository implements ItemRepository {
    private static final Map<Long, Item> items = new HashMap<>();

    @Override
    public List<Item> findByUserId(long userId) {
        return items.values()
                .stream()
                .filter(item -> item.getUserId() == userId)
                .toList();
    }

    @Override
    public Optional<Item> getItemById(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public List<Item> getItemsByText(String search) {
        if (search == null || search.trim().isEmpty()) {
            return List.of();
        }
        return items.values()
                .stream()
                .filter(item -> filterBySearchText(item, search))
                .filter(this::filterByAvailable)
                .toList();
    }

    @Override
    public Item save(Item item) {
        item.setId(getNextId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(long userId, long itemId, ItemDto item) {
        if (isUserHasItem(userId, itemId)) {
            Item existItem = items.get(itemId);

            if (item.getName() != null) {
                existItem.setName(item.getName());
            }

            if (item.getDescription() != null) {
                existItem.setDescription(item.getDescription());
            }

            if (item.getAvailable() != null) {
                existItem.setAvailable(item.getAvailable());
            }

            return existItem;
        }

        throw new NotFoundException("У пользователя с id = " + userId + " не найден айтем с id = " + itemId);
    }

    @Override
    public boolean isUserHasItem(long userId, long itemId) {
        Item item = items.get(itemId);
        return item != null && item.getUserId() == userId;
    }

    @Override
    public void deleteByUserIdAndItemId(long userId, long itemId) {
        Item item = items.get(itemId);

        if (item.getUserId() == userId) {
            items.remove(itemId);
        }
    }

    private long getNextId() {
        long currentMaxId = items.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private boolean filterBySearchText(Item item, String search) {
        String lowerSearch = search.toLowerCase().trim();
        return item.getName().toLowerCase().contains(lowerSearch) || item.getDescription().toLowerCase().contains(lowerSearch);
    }

    private boolean filterByAvailable(Item item) {
        return item.getAvailable();
    }
}
