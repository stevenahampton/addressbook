package com.stevenhampton.addressbook.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.validation.constraints.NotNull;

/**
 * Rather than using @ManyToMany between {@link AddressBook} and {@link Contact}, this class models that relationship
 * explicitly, which allows for easier manipulation of the relationship.
 */
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressBookContact {
    @JsonIgnore
    @EmbeddedId
    private AddressBookContactId id;

    @ManyToOne
    @MapsId("addressBookId")
    @JsonIgnoreProperties("contacts")
    private @NotNull AddressBook addressBook;

    @ManyToOne
    @MapsId("contactId")
    private @NotNull Contact contact;
}
