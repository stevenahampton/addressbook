package com.stevenhampton.addressbook.specification;

import com.stevenhampton.addressbook.model.AddressBook;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

/**
 * Build a JPA-based query for {@link AddressBook} dynamically.
 */
public class AddressBookSpecification {
    private AddressBookSpecification() {
    }

    /**
     * Builds a {@link Specification} to match on name and/or userId.
     * @param nameSearch partial match for name
     * @param userId     exact match for owner's ID
     * @return {@link Specification}
     */
    public static Specification<AddressBook> match(String nameSearch, UUID userId) {
        Specification<AddressBook> querySpecification = Specification.where(null);
        if (StringUtils.isNotBlank(nameSearch)) {
            querySpecification = SpecificationUtil.columnContains("name", nameSearch);
        }
        if (userId != null) {
            querySpecification = querySpecification.and(
                    (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("owner").get("id"), userId));
        }

        return querySpecification;
    }
}
