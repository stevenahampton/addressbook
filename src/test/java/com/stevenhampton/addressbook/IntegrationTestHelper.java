package com.stevenhampton.addressbook;

import com.stevenhampton.addressbook.exception.DataIntegrityException;
import com.stevenhampton.addressbook.model.AddressBook;
import com.stevenhampton.addressbook.model.Contact;
import com.stevenhampton.addressbook.model.User;
import com.stevenhampton.addressbook.repository.AddressBookContactRepository;
import com.stevenhampton.addressbook.repository.AddressBookRepository;
import com.stevenhampton.addressbook.repository.ContactRepository;
import com.stevenhampton.addressbook.repository.UserRepository;
import com.stevenhampton.addressbook.service.AddressBookContactService;
import com.stevenhampton.addressbook.service.AddressBookService;
import com.stevenhampton.addressbook.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
public class IntegrationTestHelper {
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private ContactService contactService;
    @Autowired
    private AddressBookContactService addressBookContactService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContactRepository contactRepository;
    @Autowired
    private AddressBookRepository addressBookRepository;
    @Autowired
    private AddressBookContactRepository addressBookContactRepository;

    public Set<User> users;
    public Set<AddressBook> addressBooks;
    public Set<Contact> contacts;

    /**
     * Clear the database of any data added for testing.
     */
    public void clearAllTables() {
        addressBookContactRepository.deleteAll();
        addressBookRepository.deleteAll();
        contactRepository.deleteAll();
        userRepository.deleteAll();
    }

    /**
     * Set up some typical test data and save it to accessible {@link Set}s.
     */
    public void setUpTestData() throws DataIntegrityException {
        var u1 = userRepository.findByUsername("olympe")
                .orElse(userRepository.save(User.builder().name("Olympe de Gouge").username("olympe").build()));
        var u2 = userRepository.findByUsername("germaine")
                .orElse(userRepository.save(User.builder().name("Germaine de Stael").username("germaine").build()));
        users = Set.of(u1, u2);

        var ab1 =
                addressBookService.persist(AddressBook.builder().name("Olympe's first address book").owner(u1).build());
        var ab2 = addressBookService.persist(
                AddressBook.builder().name("Olympe's second address book").owner(u1).build());
        var ab3 = addressBookService.persist(
                AddressBook.builder().name("Germaine's first address book").owner(u2).build());
        var ab4 = addressBookService.persist(
                AddressBook.builder().name("Germaine's second address book").owner(u2).build());
        addressBooks = Set.of(ab1, ab2, ab3, ab4);

        var c1 = contactService.persist(Contact.builder()
                .name("Germaine de Stael")
                .owner(u1)
                .phoneNumbers(Set.of("+61 411 222 333", "+61 3 8888 9999"))
                .build());
        var c2 = contactService.persist(Contact.builder()
                .name("Benjamin Constant")
                .owner(u1)
                .phoneNumbers(Set.of("+61 499 999 888", "+61 2 7777 6666"))
                .build());
        var c3 = contactService.persist(Contact.builder()
                .name("Olympe de Gouge")
                .owner(u2)
                .phoneNumbers(Set.of("+61 444 555 666", "+61 7 6666 5555"))
                .build());
        var c4 = contactService.persist(Contact.builder()
                .name("Jean-Jacques Rousseau")
                .owner(u2)
                .phoneNumbers(Set.of("+41 777 8888", "+41 22 666 5555"))
                .build());
        contacts = Set.of(c1, c2, c3, c4);

        addressBookContactService.addContactToAddressBook(ab1.getId(), c1.getId());
        addressBookContactService.addContactToAddressBook(ab1.getId(), c2.getId());
        addressBookContactService.addContactToAddressBook(ab2.getId(), c2.getId());
        addressBookContactService.addContactToAddressBook(ab3.getId(), c3.getId());
        addressBookContactService.addContactToAddressBook(ab4.getId(), c3.getId());
        addressBookContactService.addContactToAddressBook(ab4.getId(), c4.getId());
    }

    public UUID getFirstTestUserId() {
        return users.stream().findFirst().map(User::getId).orElse(null);
    }
}
