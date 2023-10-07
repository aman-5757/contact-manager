package com.assignment.contactmanager.dto.request;

import com.assignment.contactmanager.enums.SearchType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchRequestDto {
    @JsonProperty("searchString")
    private String searchString;
    @JsonProperty("searchType")
    private SearchType searchType;
}
