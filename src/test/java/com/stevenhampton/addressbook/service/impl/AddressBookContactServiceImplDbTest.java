package com.stevenhampton.addressbook.service.impl;

import com.stevenhampton.addressbook.dto.ContactDto;
import com.stevenhampton.addressbook.exception.DataIntegrityException;
import com.stevenhampton.addressbook.model.AddressBook;
import com.stevenhampton.addressbook.model.Contact;
import com.stevenhampton.addressbook.repository.AddressBookContactRepository;
import com.stevenhampton.addressbook.service.AddressBookContactService;
import com.stevenhampton.addressbook.service.AddressBookService;
import com.stevenhampton.addressbook.service.ContactService;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AddressBookContactServiceImplDbTest extends AbstractServiceImplDbTest {
    @Autowired
    private AddressBookContactRepository addressBookContactRepository;

    @Mock
    private MessageSource messageSource;
    @Mock
    private AddressBookService addressBookService;
    @Mock
    private ContactService contactService;

    private AddressBookContactService addressBookContactService;

    @BeforeEach
    void setUp() {
        addressBookContactService =
                new AddressBookContactServiceImpl(addressBookContactRepository, addressBookService, contactService,
                        messageSource);
    }

    private Pair<AddressBook, Contact> addContactAndCheck() throws DataIntegrityException {
        var newAddressBook = addAddressBookAndOwner("Address Book 99");
        var newContact = addContact(ContactDto.builder()
                .name("Another Contact")
                .ownerId(newAddressBook.getOwner().getId())
                .phoneNumbers(Set.of("+61 444 444 999"))
                .build());
        when(addressBookService.getById(any())).thenReturn(Optional.of(newAddressBook));
        when(contactService.getById(any())).thenReturn(Optional.of(newContact));

        // before contact added
        assertEquals(0, newAddressBook.getContacts().size());

        addressBookContactService.addContactToAddressBook(newAddressBook.getId(), newContact.getId());
        // need to flush/refresh to get update on parent object
        entityManager.flush();
        entityManager.refresh(newAddressBook);
        // after contact added
        assertEquals(1, newAddressBook.getContacts().size());

        return Pair.of(newAddressBook, newContact);
    }

    @Test
    void addContactToAddressBook() throws DataIntegrityException {
        addContactAndCheck();
    }

    @Test
    void removeContactFromAddressBook() throws DataIntegrityException {
        var bookAndContact = addContactAndCheck();
        var newAddressBook = bookAndContact.getLeft();
        addressBookContactService.removeContactFromAddressBook(newAddressBook.getId(),
                bookAndContact.getRight().getId());
        entityManager.flush();
        assertFalse(addressBookContactRepository.findById(newAddressBook.getId(), bookAndContact.getRight().getId())
                .isPresent());
    }

    @Test
    void getUniqueContacts() throws DataIntegrityException {
        var ab1 = addAddressBookAndOwner("Address Book 1");
        var ab2 = addAddressBookWithOwner("AddressBook 2", ab1.getOwner());
        var ab3 = addAddressBookWithOwner("Address Book 3", ab1.getOwner());
        var c1 = addContact(ContactDto.builder().name("Contact 1").ownerId(ab1.getOwner().getId()).build());
        var c2 = addContact(ContactDto.builder().name("Contact 2").ownerId(ab1.getOwner().getId()).build());
        when(addressBookService.getById(ab1.getId())).thenReturn(Optional.of(ab1));
        when(addressBookService.getById(ab2.getId())).thenReturn(Optional.of(ab2));
        when(addressBookService.getById(ab3.getId())).thenReturn(Optional.of(ab3));
        when(contactService.getById(c1.getId())).thenReturn(Optional.of(c1));
        when(contactService.getById(c2.getId())).thenReturn(Optional.of(c2));

        addressBookContactService.addContactToAddressBook(ab1.getId(), c1.getId());
        addressBookContactService.addContactToAddressBook(ab2.getId(), c1.getId());
        addressBookContactService.addContactToAddressBook(ab3.getId(), c1.getId());
        addressBookContactService.addContactToAddressBook(ab1.getId(), c2.getId());

        var results = addressBookContactService.getUniqueContacts(Set.of(ab1.getId(), ab2.getId(), ab3.getId()));
        assertNotNull(results);
        assertEquals(2, results.size());
        assertThat(results, containsInAnyOrder(hasProperty("name", equalTo("Contact 1")),
                hasProperty("name", equalTo("Contact 2"))));
    }

    @Test
    void mismatchedOwnerIds() throws DataIntegrityException {
        var ab1 = addAddressBookAndOwner("Address Book 1");
        var u2 = addOwner("Other Owner", "other");
        var c1 = addContact(ContactDto.builder().name("Contact 1 for first").ownerId(ab1.getOwner().getId()).build());
        var c2 = addContact(ContactDto.builder().name("Contact 2 for other").ownerId(u2.getId()).build());
        when(addressBookService.getById(ab1.getId())).thenReturn(Optional.of(ab1));
        when(contactService.getById(c1.getId())).thenReturn(Optional.of(c1));
        when(contactService.getById(c2.getId())).thenReturn(Optional.of(c2));

        addressBookContactService.addContactToAddressBook(ab1.getId(), c1.getId());
        var addressBookId = ab1.getId();
        var contactId = c2.getId();
        assertThrows(DataIntegrityException.class,
                () -> addressBookContactService.addContactToAddressBook(addressBookId, contactId));
    }
}
