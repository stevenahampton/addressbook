package com.stevenhampton.addressbook;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

/**
 * Standard unit test. All unit tests can derive from this class. {@link MockitoExtension} is included but doesn't have
 * to be used.
 */
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public abstract class AbstractUnitTest {

}
