package com.stevenhampton.addressbook.repository;

import com.stevenhampton.addressbook.model.Contact;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface ContactRepository extends CrudRepository<Contact, UUID> {
    Set<Contact> findAllByOwnerIdAndAddressBookContactsEmpty(UUID userId);
}
