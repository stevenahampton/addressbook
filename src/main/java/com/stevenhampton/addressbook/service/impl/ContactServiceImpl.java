package com.stevenhampton.addressbook.service.impl;

import com.stevenhampton.addressbook.dto.ContactDto;
import com.stevenhampton.addressbook.exception.DataIntegrityException;
import com.stevenhampton.addressbook.model.Contact;
import com.stevenhampton.addressbook.repository.ContactRepository;
import com.stevenhampton.addressbook.service.ContactService;
import com.stevenhampton.addressbook.service.CrudServiceHelper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Implementation of service class for all {@link Contact}-related logic.
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ContactServiceImpl implements ContactService {
    private final @NonNull ContactRepository contactRepository;
    private final @NonNull MessageSource messageSource;
    private final @NonNull ModelMapper modelMapper;
    private final @NonNull EntityManager entityManager;

    private CrudServiceHelper<Contact, UUID, ContactDto> serviceHelper;

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() {
        serviceHelper =
                CrudServiceHelper.of(ContactDto.class, Contact.class, contactRepository, messageSource, modelMapper,
                        entityManager);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public Contact persist(Contact contact) {
        return serviceHelper.persistThenFlushAndRefresh(contact);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public Contact partialUpdate(@NonNull UUID id, @NonNull Map<String, Object> propertyMap)
            throws DataIntegrityException {
        return serviceHelper.partialUpdate(id, propertyMap);
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
    public Optional<Contact> getById(UUID id) {
        return serviceHelper.getById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Contact> getUnassignedContacts(UUID userId) {
        return contactRepository.findAllByOwnerIdAndAddressBookContactsEmpty(userId);
    }
}
