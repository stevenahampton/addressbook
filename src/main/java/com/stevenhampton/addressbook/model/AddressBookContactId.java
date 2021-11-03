package com.stevenhampton.addressbook.model;

import lombok.*;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

/**
 * Rather than having a new, generated ID for each {@link AddressBook} - {@link Contact} relationship, we use the
 * existing IDs from those two related entities to guarantee uniqueness.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class AddressBookContactId implements Serializable {
    private @NotNull UUID addressBookId;
    private @NotNull UUID contactId;
}
