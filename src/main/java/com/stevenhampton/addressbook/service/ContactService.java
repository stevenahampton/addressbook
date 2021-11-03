package com.stevenhampton.addressbook.service;

import com.stevenhampton.addressbook.exception.DataIntegrityException;
import com.stevenhampton.addressbook.model.AddressBook;
import com.stevenhampton.addressbook.model.Contact;
import com.stevenhampton.addressbook.model.User;
import lombok.NonNull;
import org.springframework.beans.factory.InitializingBean;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Service class for all {@link Contact}-related logic.
 */
public interface ContactService extends InitializingBean {
    /**
     * Save the given {@link Contact} to the data store.
     * @param contact a {@link Contact}
     * @return the newly-saved {@link Contact}
     */
    Contact persist(@NonNull Contact contact);

    /**
     * Perform a partial update on the {@link Contact} matching the given id - only the properties in the given
     * <code>propertyMap</code> will be updated.
     * @param id          {@link Contact} ID
     * @param propertyMap {@link Map} map of properties to update
     * @return the newly-updated {@link Contact}
     */
    Contact partialUpdate(@NonNull UUID id, @NonNull Map<String, Object> propertyMap) throws DataIntegrityException;

    /**
     * Remove the {@link Contact} with the given id from the data store.
     * @param id a {@link Contact} ID
     * @return the id of the removed {@link Contact}
     */
    UUID removeById(@NonNull UUID id);

    /**
     * Retrieve a single {@link Contact} matching the given ID.
     * @param id {@link Contact} ID
     * @return {@link Optional} of {@link Contact}
     */
    Optional<Contact> getById(UUID id);

    /**
     * Retrieve a {@link Set} of {@link Contact}s for a {@link User} which aren't assigned to any {@link AddressBook}.
     * @return a {@link Set} of all {@link Contact}s for a {@link User} which aren't assigned to any {@link AddressBook}
     */
    Set<Contact> getUnassignedContacts(UUID userId);
}
