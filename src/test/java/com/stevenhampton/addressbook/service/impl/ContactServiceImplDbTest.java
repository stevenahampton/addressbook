package com.stevenhampton.addressbook.service.impl;

import com.stevenhampton.addressbook.dto.ContactDto;
import com.stevenhampton.addressbook.exception.DataIntegrityException;
import com.stevenhampton.addressbook.model.AddressBookContact;
import com.stevenhampton.addressbook.model.AddressBookContactId;
import com.stevenhampton.addressbook.model.Contact;
import com.stevenhampton.addressbook.repository.ContactRepository;
import com.stevenhampton.addressbook.service.ContactService;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.EmptyResultDataAccessException;

import javax.persistence.EntityManager;
import javax.validation.ConstraintViolationException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ContactServiceImplDbTest extends AbstractServiceImplDbTest {
    @Autowired
    private ContactRepository contactRepository;
    @Autowired
    private EntityManager entityManager;

    @Mock
    private MessageSource messageSource;

    private ContactService contactService;

    @BeforeEach
    void setUp() throws Exception {
        contactService = new ContactServiceImpl(contactRepository, messageSource, new ModelMapper(), entityManager);
        contactService.afterPropertiesSet();
    }

    private Contact addStandardContact() {
        var u1 = addOwner("Test Owner", "owner");
        return addContact(ContactDto.builder()
                .name("Contact Name")
                .ownerId(u1.getId())
                .phoneNumbers(Sets.newHashSet(Set.of("+41 22 111 2222", "+33 0 123 3459")))
                .build());
    }

    @Test
    void persistOk() {
        var result = addStandardContact();
        assertNotNull(result.getId());
    }

    @Test
    void persistFail() {
        var newContact = Contact.builder().owner(addOwner("Owner 1", "owner")).build();
        assertThrows(ConstraintViolationException.class, () -> contactService.persist(newContact));
    }

    @Test
    void partialUpdate() throws DataIntegrityException {
        var contact = addStandardContact();
        var updated = contactService.partialUpdate(contact.getId(), Map.of("name", "Updated Name"));
        assertEquals(contact.getId(), updated.getId());
        assertEquals(contact.getPhoneNumbers(), updated.getPhoneNumbers());
        assertEquals("Updated Name", updated.getName());
    }

    @Test
    void partialUpdateUnknown() {
        addStandardContact();
        var unknownId = UUID.randomUUID();
        Map<String, Object> propMap = Map.of("name", "Updated Name");
        assertThrows(DataIntegrityException.class, () -> contactService.partialUpdate(unknownId, propMap));
    }

    @Test
    void removeById() {
        var contact = addStandardContact();
        var id = contactService.removeById(contact.getId());
        assertEquals(contact.getId(), id);
        assertFalse(contactService.getById(id).isPresent());
    }

    @Test
    void removeByIdUnknown() {
        var unknownId = UUID.randomUUID();
        assertThrows(EmptyResultDataAccessException.class, () -> contactService.removeById(unknownId));
    }

    @Test
    void getById() {
        var contact = addStandardContact();
        assertTrue(contactService.getById(contact.getId()).isPresent());
    }

    @Test
    void getByIdUnknown() {
        addStandardContact();
        assertFalse(contactService.getById(UUID.randomUUID()).isPresent());
    }

    @Test
    void getAllUnassigned() {
        var c1 = addStandardContact();
        var ab1 = addAddressBookAndOwner("Test Address Book");
        var c2 = addContact(ContactDto.builder().name("Contact 2").ownerId(ab1.getOwner().getId()).build());
        addressBookContactRepository.save(AddressBookContact.builder()
                .id(AddressBookContactId.builder().addressBookId(ab1.getId()).contactId(c1.getId()).build())
                .addressBook(ab1)
                .contact(c1)
                .build());
        // c1 is in an address book but c2 isn't
        var result = contactService.getUnassignedContacts(ab1.getOwner().getId());
        assertNotNull(result);
        assertEquals(1, result.size());
        var first = result.stream().findFirst().orElse(null);
        assertNotNull(first);
        assertEquals(c2.getId(), first.getId());
    }
}
