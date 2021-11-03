package com.stevenhampton.addressbook.repository;

import com.stevenhampton.addressbook.model.AddressBookContact;
import com.stevenhampton.addressbook.model.AddressBookContactId;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface AddressBookContactRepository extends CrudRepository<AddressBookContact, AddressBookContactId> {
    Set<AddressBookContact> findAllByAddressBookIdIn(Set<UUID> addressBookIds);

    @Query("select a from AddressBookContact a where a.addressBook.id = :addressBookId and a.contact.id = :contactId")
    Optional<AddressBookContact> findById(UUID addressBookId, UUID contactId);

    @Modifying
    @Query("delete from AddressBookContact a where a.addressBook.id = :addressBookId and a.contact.id = :contactId")
    void deleteById(UUID addressBookId, UUID contactId);
}
