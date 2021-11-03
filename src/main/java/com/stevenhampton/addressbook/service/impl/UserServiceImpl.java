package com.stevenhampton.addressbook.service.impl;

import com.stevenhampton.addressbook.dto.UserDto;
import com.stevenhampton.addressbook.exception.DataIntegrityException;
import com.stevenhampton.addressbook.model.User;
import com.stevenhampton.addressbook.repository.UserRepository;
import com.stevenhampton.addressbook.service.CrudServiceHelper;
import com.stevenhampton.addressbook.service.UserService;
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
import java.util.UUID;

/**
 * Implementation of service class for all {@link User}-related logic.
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {
    private final @NonNull UserRepository userRepository;
    private final @NonNull MessageSource messageSource;
    private final @NonNull ModelMapper modelMapper;
    private final @NonNull EntityManager entityManager;

    private CrudServiceHelper<User, UUID, UserDto> serviceHelper;

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() {
        serviceHelper = CrudServiceHelper.of(UserDto.class, User.class, userRepository, messageSource, modelMapper,
                entityManager);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public User persist(User user) {
        return serviceHelper.persist(user);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public User partialUpdate(@NonNull UUID id, @NonNull Map<String, Object> propertyMap)
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
    public Optional<User> getById(UUID id) {
        return serviceHelper.getById(id);
    }
}
