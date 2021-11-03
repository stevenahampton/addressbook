package com.stevenhampton.addressbook.dto;

import com.stevenhampton.addressbook.model.Contact;
import lombok.*;

import java.util.Set;
import java.util.UUID;

/**
 * Transport class for {@link Contact}.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactDto {
    private String name;
    private UUID ownerId;
    private Set<String> phoneNumbers;
}
