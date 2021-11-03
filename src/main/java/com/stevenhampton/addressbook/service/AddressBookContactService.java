package com.stevenhampton.addressbook.service;

import com.stevenhampton.addressbook.exception.DataIntegrityException;
import com.stevenhampton.addressbook.model.AddressBook;
import com.stevenhampton.addressbook.model.AddressBookContact;
import com.stevenhampton.addressbook.model.Contact;
import lombok.NonNull;

import java.util.Set;
import java.util.UUID;

/**
 * Service class for all {@link AddressBookContact}-related logic.
 */
public interface AddressBookContactService {
    /**
     * Add the given contact to the given address book
     * @param addressBookId {@link AddressBook} ID
     * @param contactId     {@link Contact} ID
     * @return the newly-added {@link AddressBookContact}
     */
    AddressBookContact addContactToAddressBook(@NonNull UUID addressBookId, @NonNull UUID contactId)
            throws DataIntegrityException;

    /**
     * Remove the given contact from the given address book
     * @param addressBookId {@link AddressBook} ID
     * @param contactId     {@link Contact} ID
     */
    void removeContactFromAddressBook(@NonNull UUID addressBookId, @NonNull UUID contactId);

    /**
     * Retrieve a {@link Set} of unique {@link Contact}s contained in the given {@link Set} of address book IDs.
     * @param addressBookIds {@link Set} of address book IDs
     * @return a {@link Set} of unique {@link Contact}s contained in the given {@link Set} of address book IDs
     */
    Set<Contact> getUniqueContacts(Set<UUID> addressBookIds);
}
