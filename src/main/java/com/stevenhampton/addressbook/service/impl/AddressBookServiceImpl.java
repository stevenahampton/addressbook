package com.stevenhampton.addressbook.service.impl;

import com.stevenhampton.addressbook.dto.AddressBookDto;
import com.stevenhampton.addressbook.exception.DataIntegrityException;
import com.stevenhampton.addressbook.model.AddressBook;
import com.stevenhampton.addressbook.repository.AddressBookRepository;
import com.stevenhampton.addressbook.service.AddressBookService;
import com.stevenhampton.addressbook.service.CrudServiceHelper;
import com.stevenhampton.addressbook.specification.AddressBookSpecification;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.*;

/**
 * Implementation of service class for all {@link AddressBook}-related logic.
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AddressBookServiceImpl implements AddressBookService {
    private final @NonNull AddressBookRepository addressBookRepository;
    private final @NonNull MessageSource messageSource;
    private final @NonNull ModelMapper modelMapper;
    private final @NonNull EntityManager entityManager;

    private CrudServiceHelper<AddressBook, UUID, AddressBookDto> serviceHelper;

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() {
        serviceHelper =
                CrudServiceHelper.of(AddressBookDto.class, AddressBook.class, addressBookRepository, messageSource,
                        modelMapper, entityManager);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public AddressBook persist(AddressBook addressBook) {
        return serviceHelper.persistThenFlushAndRefresh(addressBook);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public AddressBook partialUpdate(@NonNull UUID id, @NonNull Map<String, Object> propertyMap)
            throws DataIntegrityException {
        return serviceHelper.partialUpdate(id, propertyMap);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public AddressBook replace(UUID id, AddressBook addressBook) throws DataIntegrityException {
        return serviceHelper.replace(id, addressBook);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UUID removeById(UUID id) {
        return serviceHelper.removeById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<AddressBook> getAllByIds(Set<UUID> ids) {
        if (CollectionUtils.isNotEmpty(ids)) {
            return new HashSet<>(IterableUtils.toList(addressBookRepository.findAllById(ids)));
        } else {
            return Collections.emptySet();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<AddressBook> getAllByNameAndOwner(String nameSearch, UUID userId) {
        return new HashSet<>(addressBookRepository.findAll(AddressBookSpecification.match(nameSearch, userId)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<AddressBook> getById(UUID id) {
        return addressBookRepository.findById(id);
    }
}
