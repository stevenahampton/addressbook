package com.stevenhampton.addressbook.controller;

import com.stevenhampton.addressbook.dto.AddressBookDto;
import com.stevenhampton.addressbook.exception.DataIntegrityException;
import com.stevenhampton.addressbook.model.AddressBook;
import com.stevenhampton.addressbook.service.AddressBookService;
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
 * Endpoints for {@link AddressBook}s.
 */
@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AddressBookController {
    private final @NonNull AddressBookService addressBookService;
    private final @NonNull ModelMapper modelMapper;

    /**
     * Retrieve a single {@link AddressBook} by its ID.
     * @param id {@link AddressBook} ID
     * @return the matching {@link AddressBook}, if found
     */
    @GetMapping(value = "/addressbook/{id}")
    public ResponseEntity<AddressBook> getAddressBook(@PathVariable(name = "id", required = false) UUID id) {
        return ControllerUtil.getResponseEntityFromOptional(addressBookService.getById(id));
    }

    /**
     * Retrieve all {@link AddressBook}s matching the name and user criteria.
     * @param nameSearch partial match on address book's name
     * @param userId     exact match on address book's owner's ID
     * @return a {@link Set} of matching {@link AddressBook}s
     */
    @GetMapping(value = "/addressbooks")
    public ResponseEntity<Set<AddressBook>> getAddressBooks(
            @RequestParam(value = "nameSearch", required = false) String nameSearch,
            @RequestParam(value = "userId", required = false) UUID userId) {
        return ControllerUtil.getResponseEntity(addressBookService.getAllByNameAndOwner(nameSearch, userId));
    }

    /**
     * Add a new {@link AddressBook}.
     * @param addressBook {@link AddressBookDto} containing a name and ownerId
     * @return the newly-added {@link AddressBook}
     */
    @PostMapping(value = "/addressbook")
    public ResponseEntity<AddressBook> addAddressBook(@RequestBody AddressBookDto addressBook) {
        return ControllerUtil.getResponseEntity(
                addressBookService.persist(modelMapper.map(addressBook, AddressBook.class)));
    }

    /**
     * Delete an {@link AddressBook}.
     * @param id the id of the {@link AddressBook} to delete
     * @return the id of the deleted {@link AddressBook}
     */
    @DeleteMapping(value = "/addressbook/{id}")
    public ResponseEntity<UUID> deleteAddressBook(@PathVariable("id") UUID id) {
        return ControllerUtil.getResponseEntity(addressBookService.removeById(id));
    }

    /**
     * Partially update an {@link AddressBook} with the given properties.
     * @param id          {@link AddressBook} ID
     * @param propertyMap {@link Map} of properties to update
     * @return the updated {@link AddressBook}
     * @throws DataIntegrityException if no match found for id
     */
    @PatchMapping(value = "/addressbook/{id}")
    public ResponseEntity<AddressBook> patchAddressBook(
            @PathVariable(name = "id") UUID id, @RequestBody Map<String, Object> propertyMap)
            throws DataIntegrityException {
        return ControllerUtil.getResponseEntity(addressBookService.partialUpdate(id, propertyMap));
    }

    /**
     * Completely replace the matching {@link AddressBook} with the details from the given {@link AddressBookDto}.
     * @param id          {@link AddressBook} ID
     * @param addressBook {@link AddressBookDto}
     * @return replaced {@link AddressBook}
     * @throws DataIntegrityException if no match found for id
     */
    @PutMapping(value = "/addressbook/{id}")
    public ResponseEntity<AddressBook> replaceAddressBook(
            @PathVariable(name = "id") UUID id, @RequestBody AddressBookDto addressBook) throws DataIntegrityException {
        return ControllerUtil.getResponseEntity(
                addressBookService.replace(id, modelMapper.map(addressBook, AddressBook.class)));
    }
}
