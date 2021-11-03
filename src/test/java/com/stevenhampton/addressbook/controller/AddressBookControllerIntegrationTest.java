package com.stevenhampton.addressbook.controller;

import com.stevenhampton.addressbook.AbstractIntegrationTest;
import com.stevenhampton.addressbook.IntegrationTestHelper;
import com.stevenhampton.addressbook.dto.AddressBookDto;
import com.stevenhampton.addressbook.exception.DataIntegrityException;
import com.stevenhampton.addressbook.model.AddressBook;
import com.stevenhampton.addressbook.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AddressBookControllerIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private IntegrationTestHelper integrationTestHelper;
    @Autowired
    private AddressBookController addressBookController;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;

    @BeforeAll
    void setUp() throws DataIntegrityException {
        integrationTestHelper.clearAllTables();
        integrationTestHelper.setUpTestData();
    }

    @AfterAll
    void tearDown() {
        integrationTestHelper.clearAllTables();
    }

    @Order(1)
    @Test
    void getAddressBooks() {
        var result = addressBookController.getAddressBooks("Olympe's", null);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        var body = result.getBody();
        assertNotNull(body);
        assertEquals(2, body.size());
    }

    private AddressBook addBook() {
        var owner = userRepository.findByUsername("olympe");
        assertTrue(owner.isPresent());
        var result = addressBookController.addAddressBook(
                AddressBookDto.builder().name("Olympe's third address book").ownerId(owner.get().getId()).build());
        assertEquals(HttpStatus.OK, result.getStatusCode());
        var body = result.getBody();
        assertNotNull(body);
        assertNotNull(body.getId());
        return body;
    }

    @Test
    void addAddressBook() {
        addBook();
    }

    @Test
    void replaceAddressBook() throws DataIntegrityException {
        var newBook = addBook();
        var dto = modelMapper.map(newBook, AddressBookDto.class);
        dto.setName("Updated 3rd book");
        var result = addressBookController.replaceAddressBook(newBook.getId(), dto);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        var body = result.getBody();
        assertNotNull(body);
        assertEquals("Updated 3rd book", body.getName());
    }

    @Test
    void deleteAddressBook() {
        var books = addressBookController.getAddressBooks(null, integrationTestHelper.getFirstTestUserId());
        assertNotNull(books);
        var body = books.getBody();
        assertNotNull(body);
        assertFalse(body.isEmpty());
        var firstId = body.stream().findFirst().map(AddressBook::getId).orElse(null);
        assertNotNull(firstId);
        var id = addressBookController.deleteAddressBook(firstId).getBody();
        assertEquals(firstId, id);
        var response = addressBookController.getAddressBooks(null, integrationTestHelper.getFirstTestUserId());
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(body.size() - 1, response.getBody().size());
    }
}
