package com.stevenhampton.addressbook.controller;

import com.stevenhampton.addressbook.AbstractIntegrationTest;
import com.stevenhampton.addressbook.IntegrationTestHelper;
import com.stevenhampton.addressbook.dto.ContactDto;
import com.stevenhampton.addressbook.exception.DataIntegrityException;
import com.stevenhampton.addressbook.model.Contact;
import com.stevenhampton.addressbook.model.User;
import com.stevenhampton.addressbook.repository.ContactRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ContactControllerIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private IntegrationTestHelper integrationTestHelper;
    @Autowired
    private ContactRepository contactRepository;
    @Autowired
    private ContactController contactController;

    @BeforeAll
    void setUp() throws DataIntegrityException {
        integrationTestHelper.clearAllTables();
        integrationTestHelper.setUpTestData();
    }

    @AfterAll
    void tearDown() {
        integrationTestHelper.clearAllTables();
    }

    private Contact addTestContact(String name, UUID ownerId) {
        var response = contactController.addContact(ContactDto.builder().name(name).ownerId(ownerId).build());
        assertEquals(OK, response.getStatusCode());
        var body = response.getBody();
        assertNotNull(body);
        assertEquals(name, body.getName());
        return body;
    }

    @Order(1)
    @Test
    void getUnassigned() {
        var id1 = integrationTestHelper.getFirstTestUserId();
        var response = contactController.getUnassignedContacts(id1);
        assertEquals(NOT_FOUND, response.getStatusCode());

        var testContact = addTestContact("Johann Jakob Bodmer", id1);
        response = contactController.getUnassignedContacts(id1);
        assertEquals(OK, response.getStatusCode());

        var body = response.getBody();
        assertNotNull(body);
        assertEquals(1, body.size());
        var first = body.stream().findFirst().orElse(null);
        assertNotNull(first);
        assertEquals(testContact.getId(), first.getId());
    }

    @Test
    void addContact() {
        addTestContact("Johann Jakob Breitinger", integrationTestHelper.getFirstTestUserId());
    }

    @Test
    void deleteContact() {
        addTestContact("Albrecht von Haller", integrationTestHelper.getFirstTestUserId());
        var unassigned = contactController.getUnassignedContacts(
                integrationTestHelper.users.stream().findFirst().map(User::getId).orElse(null));
        assertNotNull(unassigned);
        var body = unassigned.getBody();
        assertNotNull(body);
        assertFalse(body.isEmpty());
        var first = body.stream().findFirst().orElse(null);
        assertNotNull(first);
        contactController.deleteContact(first.getId());

        unassigned = contactController.getUnassignedContacts(
                integrationTestHelper.users.stream().findFirst().map(User::getId).orElse(null));
        assertNotNull(unassigned);
        var updatedBody = unassigned.getBody();
        assertNotNull(updatedBody);
        assertEquals(body.size() - 1, updatedBody.size());
    }
}
