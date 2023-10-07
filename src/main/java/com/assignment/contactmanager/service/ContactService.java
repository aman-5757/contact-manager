package com.assignment.contactmanager.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public interface ContactService {
    ResponseEntity<?> addContact(String requestDto)throws JsonProcessingException;
    ResponseEntity<?> deleteContact(Long contactId);
    ResponseEntity<?> updateContact(Long contactId , String requestDto) throws JsonProcessingException;
    ResponseEntity<?> getContact(Long contactId);
    ResponseEntity<?> searchContacts(String requestDto) throws JsonProcessingException;
}
