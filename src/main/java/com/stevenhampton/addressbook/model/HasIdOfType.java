package com.stevenhampton.addressbook.model;

/**
 * Used to indicate that a data class has an ID of type K.
 * @param <K> the type of the ID
 */
public interface HasIdOfType<K> {
    K getId();

    void setId(K id);
}
