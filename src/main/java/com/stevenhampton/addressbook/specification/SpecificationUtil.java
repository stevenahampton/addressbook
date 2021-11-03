package com.stevenhampton.addressbook.specification;

import org.springframework.data.jpa.domain.Specification;

/**
 * {@link Specification} utility methods.
 */
public class SpecificationUtil {
    private SpecificationUtil() {
    }

    /**
     * Builds a LIKE clause
     * @param columnName name of column
     * @param searchTerm search term to be wrapped by LIKE wildcard characters
     * @param <E>        type of entity class
     * @return {@link Specification}
     */
    public static <E> Specification<E> columnContains(String columnName, String searchTerm) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get(columnName)),
                criteriaBuilder.lower(criteriaBuilder.literal(asLikePattern(searchTerm)))));
    }

    /**
     * Prepends and appends "%" to the given {@link String}.
     * @param value search term to wrap
     * @return LIKE-compatible {@link String}
     */
    public static String asLikePattern(String value) {
        return String.format("%%%s%%", value);
    }
}
