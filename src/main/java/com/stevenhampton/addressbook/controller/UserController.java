package com.stevenhampton.addressbook.controller;

import com.stevenhampton.addressbook.dto.UserDto;
import com.stevenhampton.addressbook.exception.DataIntegrityException;
import com.stevenhampton.addressbook.model.User;
import com.stevenhampton.addressbook.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * Endpoints for {@link User}s.
 */
@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {
    private final @NonNull UserService userService;
    private final @NonNull ModelMapper modelMapper;

    /**
     * Retrieve a single {@link User} by its ID.
     * @param id {@link User} ID
     * @return the matching {@link User}, if found
     */
    @GetMapping(value = "/user/{id}")
    public ResponseEntity<User> getUser(@PathVariable(name = "id", required = false) UUID id) {
        return ControllerUtil.getResponseEntityFromOptional(userService.getById(id));
    }

    /**
     * Add a new {@link User}.
     * @param user {@link UserDto} containing a name and phone numbers
     * @return the newly-added {@link User}
     */
    @PostMapping(value = "/user")
    public ResponseEntity<User> addUser(@RequestBody UserDto user) {
        return ControllerUtil.getResponseEntity(userService.persist(modelMapper.map(user, User.class)));
    }

    /**
     * Partially update a {@link User} with the given properties.
     * @param id          {@link User} ID
     * @param propertyMap {@link Map} of properties to update
     * @return the updated {@link User}
     * @throws DataIntegrityException if no match found for id
     */
    @PatchMapping(value = "/user/{id}")
    public ResponseEntity<User> patchUser(
            @PathVariable(name = "id") UUID id, @RequestBody Map<String, Object> propertyMap)
            throws DataIntegrityException {
        return ControllerUtil.getResponseEntity(userService.partialUpdate(id, propertyMap));
    }

    /**
     * Remove the {@link User} matching the given id.
     * @param id {@link User} ID
     * @return id of deleted {@link User}
     */
    @DeleteMapping(value = "/user/{id}")
    public ResponseEntity<UUID> deleteUser(@PathVariable(name = "id") UUID id) {
        return ControllerUtil.getResponseEntity(userService.removeById(id));
    }
}
