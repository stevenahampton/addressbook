package com.stevenhampton.addressbook.service.impl;

import com.stevenhampton.addressbook.exception.DataIntegrityException;
import com.stevenhampton.addressbook.model.AddressBookContact;
import com.stevenhampton.addressbook.model.AddressBookContactId;
import com.stevenhampton.addressbook.model.Contact;
import com.stevenhampton.addressbook.repository.AddressBookContactRepository;
import com.stevenhampton.addressbook.service.AddressBookContactService;
import com.stevenhampton.addressbook.service.AddressBookService;
import com.stevenhampton.addressbook.service.ContactService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of service class for all {@link AddressBookContact}-related logic.
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AddressBookContactServiceImpl implements AddressBookContactService {
    private final @NonNull AddressBookContactRepository addressBookContactRepository;
    private final @NonNull AddressBookService addressBookService;
    private final @NonNull ContactService contactService;
    private final @NonNull MessageSource messageSource;

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public AddressBookContact addContactToAddressBook(@NonNull UUID addressBookId, @NonNull UUID contactId)
            throws DataIntegrityException {
        var addressBook = addressBookService.getById(addressBookId);
        var contact = contactService.getById(contactId);
        if (addressBook.isEmpty()) {
            throw new DataIntegrityException(messageSource, "validation.unknownAddressBookId", addressBookId);
        }
        if (contact.isEmpty()) {
            throw new DataIntegrityException(messageSource, "validation.unknownContactId", contactId);
        }
        // address book and contact must have the same owner
        var addressBookOwnerId = addressBook.get().getOwner().getId();
        var contactOwnerId = contact.get().getOwner().getId();
        if (!Objects.equals(addressBookOwnerId, contactOwnerId)) {
            throw new DataIntegrityException(messageSource, "validation.mismatchedOwnerIds", addressBookOwnerId,
                    contactOwnerId);
        }
        return addressBookContactRepository.save(AddressBookContact.builder()
                .id(AddressBookContactId.builder().addressBookId(addressBookId).contactId(contactId).build())
                .addressBook(addressBook.get())
                .contact(contact.get())
                .build());
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void removeContactFromAddressBook(@NonNull UUID addressBookId, @NonNull UUID contactId) {
        addressBookContactRepository.deleteById(addressBookId, contactId);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public Set<Contact> getUniqueContacts(@NonNull Set<UUID> addressBookIds) {
        return IterableUtils.toList(addressBookContactRepository.findAllByAddressBookIdIn(addressBookIds))
                .stream()
                .map(AddressBookContact::getContact)
                .collect(Collectors.toSet());
    }
}
