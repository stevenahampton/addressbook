package com.stevenhampton.addressbook.repository;

import com.stevenhampton.addressbook.model.AddressBook;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AddressBookRepository
        extends CrudRepository<AddressBook, UUID>, JpaSpecificationExecutor<AddressBook> {
}
