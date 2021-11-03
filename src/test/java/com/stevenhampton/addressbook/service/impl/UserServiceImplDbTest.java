package com.stevenhampton.addressbook.service.impl;

import com.stevenhampton.addressbook.exception.DataIntegrityException;
import com.stevenhampton.addressbook.model.User;
import com.stevenhampton.addressbook.repository.UserRepository;
import com.stevenhampton.addressbook.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.EmptyResultDataAccessException;

import javax.persistence.EntityManager;
import javax.validation.ConstraintViolationException;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceImplDbTest extends AbstractServiceImplDbTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EntityManager entityManager;

    @Mock
    private MessageSource messageSource;

    private UserService userService;

    @BeforeEach
    void setUp() throws Exception {
        userService = new UserServiceImpl(userRepository, messageSource, new ModelMapper(), entityManager);
        userService.afterPropertiesSet();
    }

    private User addUser() {
        var result = userService.persist(User.builder().name("My Name").username("myname").build());
        assertNotNull(result.getId());
        return result;
    }

    @Test
    void persistOk() {
        addUser();
    }

    @Test
    void persistFail() {
        var result = userService.persist(User.builder().name("My Name").build());
        // have to flush to force validation
        assertThrows(ConstraintViolationException.class, () -> entityManager.flush());
    }

    @Test
    void partialUpdate() throws DataIntegrityException {
        var user = addUser();
        var updated = userService.partialUpdate(user.getId(), Map.of("name", "Updated Name"));
        assertEquals(user.getId(), updated.getId());
        assertEquals(user.getUsername(), updated.getUsername());
        assertEquals("Updated Name", updated.getName());
    }

    @Test
    void partialUpdateUnknown() {
        addUser();
        var unknownId = UUID.randomUUID();
        Map<String, Object> propMap = Map.of("name", "Updated Name");
        assertThrows(DataIntegrityException.class, () -> userService.partialUpdate(unknownId, propMap));
    }

    @Test
    void removeById() {
        var user = addUser();
        var id = userService.removeById(user.getId());
        assertEquals(user.getId(), id);
        assertFalse(userService.getById(id).isPresent());
    }

    @Test
    void removeByIdUnknown() {
        var unknownId = UUID.randomUUID();
        assertThrows(EmptyResultDataAccessException.class, () -> userService.removeById(unknownId));
    }

    @Test
    void getById() {
        var user = addUser();
        assertTrue(userService.getById(user.getId()).isPresent());
    }

    @Test
    void getByIdUnknown() {
        addUser();
        assertFalse(userService.getById(UUID.randomUUID()).isPresent());
    }
}
