package com.stevenhampton.addressbook.service;

import com.stevenhampton.addressbook.exception.DataIntegrityException;
import com.stevenhampton.addressbook.model.HasIdOfType;
import lombok.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Map;
import java.util.Optional;

/**
 * Common, generified helper class which provides a standard set of CRUD methods for service classes.
 * @param <T> type of entity class
 * @param <K> type of entity class's id
 * @param <D> type of DTO class
 */
public class CrudServiceHelper<T extends HasIdOfType<K>, K, D> {
    private final CrudRepository<T, K> repository;
    private final MessageSource messageSource;
    private final Class<D> dtoClass;
    private final Class<T> entityClass;
    private final ModelMapper modelMapper;
    private final EntityManager entityManager;

    /**
     * Get a new instance matched to the types supplied.
     * @param dtoClass      class for DTO
     * @param entityClass   class for entity
     * @param repository    instance of repository
     * @param messageSource {@link MessageSource}
     * @param modelMapper   {@link ModelMapper}
     * @param entityManager {@link EntityManager}
     * @param <T>           entity type which extends {@link HasIdOfType}
     * @param <K>           entity's id type
     * @param <D>           DTO type
     * @return a new instance of {@link CrudServiceHelper}
     */
    public static <T extends HasIdOfType<K>, K, D> CrudServiceHelper<T, K, D> of(
            Class<D> dtoClass,
            Class<T> entityClass,
            CrudRepository<T, K> repository,
            MessageSource messageSource,
            ModelMapper modelMapper,
            EntityManager entityManager) {
        return new CrudServiceHelper<>(dtoClass, entityClass, repository, messageSource, modelMapper, entityManager);
    }

    /**
     * @param dtoClass      class for DTO
     * @param entityClass   class for entity
     * @param repository    instance of repository
     * @param messageSource {@link MessageSource}
     * @param modelMapper   {@link ModelMapper}
     * @param entityManager {@link EntityManager}
     */
    private CrudServiceHelper(
            Class<D> dtoClass,
            Class<T> entityClass,
            CrudRepository<T, K> repository,
            MessageSource messageSource,
            ModelMapper modelMapper,
            EntityManager entityManager) {
        this.dtoClass = dtoClass;
        this.entityClass = entityClass;
        this.repository = repository;
        this.messageSource = messageSource;
        this.modelMapper = modelMapper;
        this.entityManager = entityManager;
    }

    /**
     * Save the given entity to the data store.
     * @param entity object to persist
     * @return the saved entity
     */
    @Transactional
    public T persist(T entity) {
        return repository.save(entity);
    }

    /**
     * Save the given entity to the data store, followed by flushing and refreshing, to ensure all sub-objects in the
     * entity are repopulated.
     * @param entity object to persist
     * @return the saved entity
     */
    @Transactional
    public T persistThenFlushAndRefresh(T entity) {
        var saved = persist(entity);
        // flush & refresh so any sub-objects are populated
        entityManager.flush();
        entityManager.refresh(saved);
        return saved;
    }

    /**
     * Perform a partial update on the entity matching the given id - only the properties in the given
     * <code>propertyMap</code> will be updated.
     * @param id          entity ID
     * @param propertyMap {@link Map} map of properties to update
     * @return the newly-updated entity
     */
    @Transactional
    public T partialUpdate(@NonNull K id, @NonNull Map<String, Object> propertyMap) throws DataIntegrityException {
        var existing = getById(id);
        if (existing.isEmpty()) {
            throw new DataIntegrityException(messageSource, "validation.unknownId", entityClass.getSimpleName(), id);
        } else {
            var entity = existing.get();
            // go via dto to limit to only those properties we want to allow changed
            var dto = modelMapper.map(propertyMap, dtoClass);
            modelMapper.map(dto, entity);
            return persist(entity);
        }
    }

    /**
     * Update the given entity in the data store by replacing it completely.
     * @param id     the ID of the entity to update
     * @param entity an entity (id property will be ignored in favour of separate argument)
     * @return the newly-updated entity
     */
    @Transactional
    public T replace(K id, T entity) throws DataIntegrityException {
        var existing = repository.findById(id);
        if (existing.isEmpty()) {
            throw new DataIntegrityException(messageSource, "validation.unknownId", entityClass.getSimpleName(), id);
        } else {
            entity.setId(id);
            return persist(entity);
        }
    }

    /**
     * Remove the entity with the given id from the data store.
     * @param id entity ID
     * @return the id of the removed entity
     */
    public K removeById(K id) {
        repository.deleteById(id);
        return id;
    }

    /**
     * Retrieve a single entity matching the given ID.
     * @param id entity ID
     * @return {@link Optional} of the entity
     */
    public Optional<T> getById(K id) {
        return repository.findById(id);
    }
}
