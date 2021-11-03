package com.stevenhampton.addressbook.service.impl;

import com.stevenhampton.addressbook.exception.DataIntegrityException;
import com.stevenhampton.addressbook.model.AddressBook;
import com.stevenhampton.addressbook.model.User;
import com.stevenhampton.addressbook.service.AddressBookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;

import javax.validation.ConstraintViolationException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AddressBookServiceImplDbTest extends AbstractServiceImplDbTest {
    @Mock
    private MessageSource messageSource;

    private AddressBookService addressBookService;

    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() throws Exception {
        modelMapper = new ModelMapper();
        addressBookService =
                new AddressBookServiceImpl(addressBookRepository, messageSource, modelMapper, entityManager);
        addressBookService.afterPropertiesSet();
    }

    @SuppressWarnings("java:S2699")
    @Test
    void persistOk() {
        var owner = addOwner("Test Owner", "owner");
        // test with just the ownerId, like from DTO
        var ab1 = addressBookService.persist(AddressBook.builder()
                .name("Address Book Without Owner")
                .owner(User.builder().id(owner.getId()).build())
                .build());
        // owner should be fully populated in return value
        assertNotNull(ab1.getId());
        assertEquals("owner", ab1.getOwner().getUsername());
        assertEquals("Test Owner", ab1.getOwner().getName());
    }

    @Test
    void persistFailsWithoutOwner() {
        var addressBook = AddressBook.builder().name("Address Book Without Owner").build();
        assertThrows(ConstraintViolationException.class, () -> addressBookService.persist(addressBook));
    }

    @Test
    void replace() throws DataIntegrityException {
        var addressBook = addAddressBookAndOwner("Address Book 1");
        var updated = addressBookService.replace(addressBook.getId(),
                AddressBook.builder().name("Updated Name").owner(addressBook.getOwner()).build());
        assertEquals(addressBook.getId(), updated.getId());
        assertEquals(addressBook.getOwner(), updated.getOwner());
        assertEquals("Updated Name", updated.getName());
    }

    @Test
    void replaceUnknown() {
        var addressBook = addAddressBookAndOwner("Address Book 1");
        var unknownId = UUID.randomUUID();
        var dto = AddressBook.builder().name("Updated Name").owner(addressBook.getOwner()).build();
        assertThrows(DataIntegrityException.class,
                () -> addressBookService.replace(unknownId, modelMapper.map(dto, AddressBook.class)));
    }

    @Test
    void partialUpdate() throws DataIntegrityException {
        var addressBook = addAddressBookAndOwner("Address Book 1");
        var updated = addressBookService.partialUpdate(addressBook.getId(), Map.of("name", "Updated Name"));
        assertEquals(addressBook.getId(), updated.getId());
        assertEquals(addressBook.getOwner(), updated.getOwner());
        assertEquals("Updated Name", updated.getName());
    }

    @Test
    void partialUpdateUnknown() {
        addAddressBookAndOwner("Address Book 1");
        var unknownId = UUID.randomUUID();
        Map<String, Object> propMap = Map.of("name", "Updated Name");
        assertThrows(DataIntegrityException.class, () -> addressBookService.partialUpdate(unknownId, propMap));
    }

    @Test
    void getById() {
        var addressBook = addAddressBookAndOwner("Address Book 1");
        assertTrue(addressBookService.getById(addressBook.getId()).isPresent());
    }

    @Test
    void getByIdUnknown() {
        addAddressBookAndOwner("Address Book 1");
        assertFalse(addressBookService.getById(UUID.randomUUID()).isPresent());
    }

    @Test
    void getAllByIds() {
        var addressBook = addAddressBookAndOwner("Address Book 1");
        var results = addressBookService.getAllByIds(Set.of(addressBook.getId(), UUID.randomUUID()));
        assertNotNull(results);
        assertEquals(1, results.size());
    }

    @Test
    void getAllByIdsEmptyList() {
        addAddressBookAndOwner("Address Book 1");
        var results = addressBookService.getAllByIds(Set.of());
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    void getAllByNameOnly() {
        var addressBook1 = addAddressBookAndOwner("Address Book 1");
        var addressBook2 = addAddressBookWithOwner("Address Book 2", addressBook1.getOwner());
        var results = addressBookService.getAllByNameAndOwner("Book 1", null);
        assertNotNull(results);
        assertEquals(1, results.size());
    }

    @Test
    void getAllByOwnerOnly() {
        var addressBook1 = addAddressBookAndOwner("Address Book 1");
        var addressBook2 = addAddressBookWithOwner("Address Book 2", addressBook1.getOwner());
        var owner2 = addOwner("Second Owner", "second");
        var addressBook3 = addAddressBookWithOwner("Address Book 3", owner2);
        var results = addressBookService.getAllByNameAndOwner(null, owner2.getId());
        assertNotNull(results);
        assertEquals(1, results.size());
        var firstResult = results.stream().findFirst();
        assertTrue(firstResult.isPresent());
        assertEquals(owner2.getId(), firstResult.get().getOwner().getId());
    }

    @Test
    void getAllByNameAndOwner() {
        var addressBook = addAddressBookAndOwner("Address Book 1");
        var owner2 = addOwner("Second Owner", "second");
        addAddressBookWithOwner("Address Book 2", owner2);
        var results = addressBookService.getAllByNameAndOwner("Book", addressBook.getOwner().getId());
        assertNotNull(results);
        assertEquals(1, results.size());
        var firstResult = results.stream().findFirst();
        assertTrue(firstResult.isPresent());
        assertEquals(addressBook.getOwner().getId(), firstResult.get().getOwner().getId());
    }
}
