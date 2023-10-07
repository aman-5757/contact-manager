package com.assignment.contactmanager.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddUserRequestDto {
    @JsonProperty("email")
    private String email;
    @JsonProperty("password")
    private String password;
}
