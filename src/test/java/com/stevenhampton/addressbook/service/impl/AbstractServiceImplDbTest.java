package com.stevenhampton.addressbook.service.impl;

import com.stevenhampton.addressbook.AbstractDbTest;
import com.stevenhampton.addressbook.dto.ContactDto;
import com.stevenhampton.addressbook.model.AddressBook;
import com.stevenhampton.addressbook.model.Contact;
import com.stevenhampton.addressbook.model.User;
import com.stevenhampton.addressbook.repository.AddressBookContactRepository;
import com.stevenhampton.addressbook.repository.AddressBookRepository;
import com.stevenhampton.addressbook.repository.ContactRepository;
import com.stevenhampton.addressbook.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public abstract class AbstractServiceImplDbTest extends AbstractDbTest {
    @Autowired
    protected AddressBookRepository addressBookRepository;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected ContactRepository contactRepository;
    @Autowired
    protected AddressBookContactRepository addressBookContactRepository;
    @Autowired
    protected EntityManager entityManager;

    protected Contact addContact(ContactDto contactDto) {
        return contactRepository.save(new ModelMapper().map(contactDto, Contact.class));
    }

    protected User addOwner(String name, String username) {
        return userRepository.save(User.builder().name(name).username(username).build());
    }

    protected AddressBook addAddressBookAndOwner(String name) {
        var owner = addOwner("The Owner", "owner1");
        var result = addressBookRepository.save(AddressBook.builder().name(name).owner(owner).build());
        assertNotNull(result.getId());
        return result;
    }

    protected AddressBook addAddressBookWithOwner(String name, User owner) {
        var result = addressBookRepository.save(AddressBook.builder().name(name).owner(owner).build());
        assertNotNull(result.getId());
        return result;
    }
}
