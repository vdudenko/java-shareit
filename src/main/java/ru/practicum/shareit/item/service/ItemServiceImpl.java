package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public List<Item> getItems(long userId) {
        return itemRepository.findByUserId(userId);
    }

    @Override
    public Optional<Item> getItem(Long itemId) {
        return itemRepository.getItemById(itemId);
    }

    @Override
    public List<Item> getItemsByText(String search) {
        return itemRepository.getItemsByText(search);
    }

    @Override
    public Item addNewItem(Long userId, Item item) {
        if(userRepository.isUserExist(userId)) {
            item.setUserId(userId);
            return itemRepository.save(item);
        }
        throw new NotFoundException("Пользователь с id = " + userId + " не найден");
    }

    @Override
    public Item updateItem(Long userId, Long itemId, ItemDto item) {
        if(userRepository.isUserExist(userId)) {
            return itemRepository.update(userId, itemId, item);
        }
        throw new NotFoundException("Пользователь с id = " + userId + " не найден");
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        itemRepository.deleteByUserIdAndItemId(userId, itemId);
    }
}
