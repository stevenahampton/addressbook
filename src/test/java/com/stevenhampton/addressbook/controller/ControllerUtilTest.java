package com.stevenhampton.addressbook.controller;

import com.stevenhampton.addressbook.AbstractUnitTest;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

class ControllerUtilTest extends AbstractUnitTest {

    @Test
    void getStatusForNull() {
        assertEquals(OK, ControllerUtil.getStatus(null));
    }

    @Test
    void getStatusForEmptyOptional() {
        assertEquals(NOT_FOUND, ControllerUtil.getStatus(Optional.empty()));
    }

    @Test
    void getStatusForEmptyIterable() {
        assertEquals(NOT_FOUND, ControllerUtil.getStatus(List.of()));
    }

    @Test
    void getResponseEntityFromOptionalEmpty() {
        var response = ControllerUtil.getResponseEntityFromOptional(Optional.empty());
        assertEquals(NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getResponseEntityFromOptional() {
        var response = ControllerUtil.getResponseEntityFromOptional(Optional.of("Hi!"));
        assertEquals(OK, response.getStatusCode());
        assertEquals("Hi!", response.getBody());
    }

    @Test
    void getResponseEntityForNull() {
        var response = ControllerUtil.getResponseEntity(null);
        assertEquals(OK, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getResponseEntity() {
        var response = ControllerUtil.getResponseEntity("Hi!");
        assertEquals(OK, response.getStatusCode());
        assertEquals("Hi!", response.getBody());
    }
}
