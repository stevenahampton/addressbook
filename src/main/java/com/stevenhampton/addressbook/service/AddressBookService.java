package com.stevenhampton.addressbook.service;

import com.stevenhampton.addressbook.exception.DataIntegrityException;
import com.stevenhampton.addressbook.model.AddressBook;
import com.stevenhampton.addressbook.model.User;
import lombok.NonNull;
import org.springframework.beans.factory.InitializingBean;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Service class for all {@link AddressBook}-related logic.
 */
public interface AddressBookService extends InitializingBean {
    /**
     * Save the given {@link AddressBook} to the data store.
     * @param addressBook an {@link AddressBook}
     * @return the newly-saved {@link AddressBook}
     */
    AddressBook persist(@NonNull AddressBook addressBook);

    /**
     * Perform a partial update on the {@link AddressBook} matching the given id - only the properties in the given
     * <code>propertyMap</code> will be updated.
     * @param id          {@link AddressBook} ID
     * @param propertyMap {@link Map} map of properties to update
     * @return the newly-updated {@link AddressBook}
     */
    AddressBook partialUpdate(@NonNull UUID id, @NonNull Map<String, Object> propertyMap) throws DataIntegrityException;

    /**
     * Update the given {@link AddressBook} in the data store by replacing it completely.
     * @param id          the ID of the {@link AddressBook} to update
     * @param addressBook an {@link AddressBook} (id property will be ignored in favour of separate argument)
     * @return the newly-updated {@link AddressBook}
     */
    AddressBook replace(@NonNull UUID id, @NonNull AddressBook addressBook) throws DataIntegrityException;

    /**
     * Remove the {@link AddressBook} with the given id from the data store.
     * @param id a {@link AddressBook} ID
     * @return the id of the removed {@link AddressBook}
     */
    UUID removeById(@NonNull UUID id);

    /**
     * Retrieve {@link AddressBook}s with the given {@link AddressBook} IDs
     * @param ids {@link Set} of {@link AddressBook} IDs
     * @return all matching {@link AddressBook}s
     */
    Set<AddressBook> getAllByIds(Set<UUID> ids);

    /**
     * Retrieve {@link AddressBook}s matching the supplied search criteria.
     * @param nameSearch partial match for {@link AddressBook} name
     * @param userId     {@link User} ID
     * @return all matching {@link AddressBook}s
     */
    Set<AddressBook> getAllByNameAndOwner(String nameSearch, UUID userId);

    /**
     * Retrieve a single {@link AddressBook} matching the given ID.
     * @param id {@link AddressBook} ID
     * @return {@link Optional} of {@link AddressBook}
     */
    Optional<AddressBook> getById(UUID id);
}
