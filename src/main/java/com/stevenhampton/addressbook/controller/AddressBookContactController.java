package com.stevenhampton.addressbook.controller;

import com.stevenhampton.addressbook.exception.DataIntegrityException;
import com.stevenhampton.addressbook.model.AddressBook;
import com.stevenhampton.addressbook.model.AddressBookContact;
import com.stevenhampton.addressbook.model.Contact;
import com.stevenhampton.addressbook.service.AddressBookContactService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

/**
 * Handle requests for maintaining the associations between {@link AddressBook}s and {@link Contact}s.
 */
@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AddressBookContactController {
    private final @NonNull AddressBookContactService addressBookContactService;

    /**
     * Add the given contact to the given address book.
     * @param addressBookId {@link AddressBook} ID
     * @param contactId     {@link Contact} ID
     * @return {@link ResponseEntity} containing an {@link AddressBookContact}
     */
    @PostMapping(value = "/addressbook/{addressBookId}/contact/{contactId}")
    public ResponseEntity<AddressBookContact> addContactToAddressBook(
            @PathVariable(name = "addressBookId") UUID addressBookId, @PathVariable(name = "contactId") UUID contactId)
            throws DataIntegrityException {
        return ControllerUtil.getResponseEntity(
                addressBookContactService.addContactToAddressBook(addressBookId, contactId));
    }

    /**
     * Remove the given contact from the given address book (or do nothing if it isn't in that address book).
     * @param addressBookId {@link AddressBook} ID
     * @param contactId     {@link Contact} ID
     * @return empty {@link ResponseEntity}
     */
    @DeleteMapping(value = "/addressbook/{addressBookId}/contact/{contactId}")
    public ResponseEntity<Void> removeContactFromAddressBook(
            @PathVariable(name = "addressBookId") UUID addressBookId,
            @PathVariable(name = "contactId") UUID contactId) {
        addressBookContactService.removeContactFromAddressBook(addressBookId, contactId);
        return ResponseEntity.ok(null);
    }

    /**
     * Get a list of unique {@link Contact}s for the given list of {@link AddressBook} IDs.
     * @param addressBookIds {@link Set} of address book IDs
     * @return {@link ResponseEntity} containing a {@link Set} of unique {@link Contact}s
     */
    @GetMapping(value = "/addressbooks/contacts")
    public ResponseEntity<Set<Contact>> getUniqueContacts(@RequestBody Set<UUID> addressBookIds) {
        return ControllerUtil.getResponseEntity(addressBookContactService.getUniqueContacts(addressBookIds));
    }
}
