package com.stevenhampton.addressbook.dto;

import com.stevenhampton.addressbook.model.AddressBook;
import lombok.*;

import java.util.UUID;

/**
 * Transport class for {@link AddressBook}.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressBookDto {
    private String name;
    private UUID ownerId;
}
