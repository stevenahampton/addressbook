package com.stevenhampton.addressbook.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Address books containing lists of contacts.
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressBook implements HasIdOfType<UUID> {
    @Id
    @GeneratedValue
    private UUID id;

    private @NotNull String name;

    @ManyToOne
    private @NotNull User owner;

    @JsonIgnoreProperties({"id", "addressBook"})
    @Builder.Default
    @OneToMany(mappedBy = "addressBook", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<AddressBookContact> contacts = new HashSet<>();
}
