package com.stevenhampton.addressbook.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

/**
 * Contact details stored in address books.
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Contact implements HasIdOfType<UUID> {
    @Id
    @GeneratedValue
    private UUID id;

    private @NotNull String name;

    @ManyToOne
    private @NotNull User owner;

    @ElementCollection
    private Set<String> phoneNumbers;

    @JsonIgnore // not needed for serialisation
    @OneToMany(mappedBy = "contact", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<AddressBookContact> addressBookContacts;
}
