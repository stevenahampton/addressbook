package com.stevenhampton.addressbook.dto;

import com.stevenhampton.addressbook.model.User;
import lombok.*;

/**
 * Transport class for {@link User}.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String name;
    private String username;
}
