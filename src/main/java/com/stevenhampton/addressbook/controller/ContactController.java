package com.stevenhampton.addressbook.controller;

import com.stevenhampton.addressbook.dto.ContactDto;
import com.stevenhampton.addressbook.exception.DataIntegrityException;
import com.stevenhampton.addressbook.model.AddressBook;
import com.stevenhampton.addressbook.model.Contact;
import com.stevenhampton.addressbook.model.User;
import com.stevenhampton.addressbook.service.ContactService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Endpoints for {@link Contact}s.
 */
@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ContactController {
    private final @NonNull ContactService contactService;
    private final @NonNull ModelMapper modelMapper;

    /**
     * Add a new {@link Contact}.
     * @param contact {@link ContactDto} containing a name and phone numbers
     * @return the newly-added {@link Contact}
     */
    @PostMapping(value = "/contact")
    public ResponseEntity<Contact> addContact(@RequestBody ContactDto contact) {
        return ControllerUtil.getResponseEntity(contactService.persist(modelMapper.map(contact, Contact.class)));
    }

    /**
     * Partially update a {@link Contact} with the given properties.
     * @param id          {@link Contact} ID
     * @param propertyMap {@link Map} of properties to update
     * @return the updated {@link Contact}
     * @throws DataIntegrityException if no match found for id
     */
    @PatchMapping(value = "/contact/{id}")
    public ResponseEntity<Contact> patchContact(
            @PathVariable(name = "id") UUID id, @RequestBody Map<String, Object> propertyMap)
            throws DataIntegrityException {
        return ControllerUtil.getResponseEntity(contactService.partialUpdate(id, propertyMap));
    }

    /**
     * Remove the {@link Contact} matching the given id.
     * @param id {@link Contact} ID
     * @return id of deleted {@link Contact}
     */
    @DeleteMapping(value = "/contact/{id}")
    public ResponseEntity<UUID> deleteContact(@PathVariable(name = "id") UUID id) {
        return ControllerUtil.getResponseEntity(contactService.removeById(id));
    }

    /**
     * Retrieve all {@link Contact}s for a {@link User} which aren't contained in an {@link AddressBook}.
     * @return a {@link Set} of all {@link Contact}s for a {@link User} which aren't contained in an {@link AddressBook}
     */
    @GetMapping(value = "/user/{id}/unassigned-contacts")
    public ResponseEntity<Set<Contact>> getUnassignedContacts(@PathVariable("id") UUID userId) {
        return ControllerUtil.getResponseEntity(contactService.getUnassignedContacts(userId));
    }
}
