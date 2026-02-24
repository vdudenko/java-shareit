package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;


    private User user;
    private Item item1;
    private Item item2;
    private ItemRequest itemRequest;

    @BeforeEach
    void create() {
        user = User.builder()
                .name("vadim")
                .email("dudenko.vadim@gmail.com")
                .build();
        userRepository.save(user);

        item1 = Item.builder()
                .name("test")
                .description("desc")
                .owner(user)
                .available(true)
                .build();
        itemRepository.save(item1);

        item2 = Item.builder()
                .name("test 2")
                .description("desc 2")
                .owner(user)
                .available(true)
                .build();
        itemRepository.save(item2);
    }

    @Test
    void getAll() {
        List<Item> expected = List.of(item1, item2);
        List<Item> result = itemRepository.findByOwnerId(user.getId());
        assertEquals(expected, result);
        assertEquals(2, result.size());
    }

    @Test
    void getItem() {
        Optional<Item> item = itemRepository.findById(item1.getId());
        assertThat(item).contains(item1);
    }

    @Test
    void getItemSearch() {
        List<Item> expected = List.of(item1, item2);

        List<Item> actual1 = itemRepository.getItemsByText("test");
        assertEquals(expected, actual1);
        assertEquals(2, actual1.size());

        List<Item> actual2 = itemRepository.getItemsByText("TEST");
        assertEquals(expected, actual2);
        assertEquals(2, actual2.size());


        List<Item> actual3 = itemRepository.getItemsByText("desc");
        assertEquals(expected, actual3);
        assertEquals(2, actual3.size());
    }

    @Test
    void add() {
        Item item = Item.builder()
                .name("Test")
                .available(true)
                .owner(user)
                .description("test description")
                .build();

        Item createdItem = itemRepository.save(item);

        assertEquals(createdItem.getName(), item.getName());
        assertEquals(createdItem.getDescription(), item.getDescription());

    }

    @Test
    void update() {
        Item item = Item.builder()
                .name("Test")
                .available(true)
                .owner(user)
                .description("test description")
                .build();

        Item createdItem = itemRepository.save(item);
        createdItem.setName("Test 2");
        Item updatedItem = itemRepository.save(createdItem);
        assertEquals(updatedItem.getName(), "Test 2");
    }

    @Test
    void deleteItem() {
        itemRepository.deleteByIdAndOwnerId(item2.getId(), user.getId());

        Optional<Item> item = itemRepository.findById(item2.getId());
        assertThat(item).isEmpty();
    }
}
