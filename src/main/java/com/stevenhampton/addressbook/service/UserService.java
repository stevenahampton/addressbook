package com.stevenhampton.addressbook.service;

import com.stevenhampton.addressbook.exception.DataIntegrityException;
import com.stevenhampton.addressbook.model.User;
import lombok.NonNull;
import org.springframework.beans.factory.InitializingBean;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Service class for all {@link User}-related logic.
 */
public interface UserService extends InitializingBean {
    /**
     * Save the given {@link User} to the data store.
     * @param user a {@link User}
     * @return the newly-saved {@link User}
     */
    User persist(@NonNull User user);

    /**
     * Perform a partial update on the {@link User} matching the given id - only the properties in the given
     * <code>propertyMap</code> will be updated.
     * @param id          {@link User} ID
     * @param propertyMap {@link Map} map of properties to update
     * @return the newly-updated {@link User}
     */
    User partialUpdate(@NonNull UUID id, @NonNull Map<String, Object> propertyMap) throws DataIntegrityException;

    /**
     * Remove the {@link User} with the given id from the data store.
     * @param id a {@link User} ID
     * @return the id of the removed {@link User}
     */
    UUID removeById(@NonNull UUID id);

    /**
     * Retrieve a single {@link User} matching the given ID.
     * @param id {@link User} ID
     * @return {@link Optional} of {@link User}
     */
    Optional<User> getById(UUID id);
}
