package com.stevenhampton.addressbook;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * The full monty - complete application context and a real database.
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"test", "integration-test"})
public abstract class AbstractIntegrationTest extends AbstractUnitTest {
}
