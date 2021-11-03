package com.stevenhampton.addressbook.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Users of the application.
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements HasIdOfType<UUID> {
    @Id
    @GeneratedValue
    private UUID id;

    private @NotNull String name;

    @Column(unique = true)
    private @NotNull String username;
}
