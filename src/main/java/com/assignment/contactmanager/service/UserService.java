package com.assignment.contactmanager.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<?> addUser(String requestDto)throws JsonProcessingException;
}
