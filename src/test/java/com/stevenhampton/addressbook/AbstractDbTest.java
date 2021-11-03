package com.stevenhampton.addressbook;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * All unit tests derived from this class can use the real repository classes but nothing else.
 */
@DataJpaTest
@ActiveProfiles({"test", "db-test"})
public abstract class AbstractDbTest extends AbstractUnitTest {
}
