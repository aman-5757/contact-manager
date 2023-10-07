package com.assignment.contactmanager.controller;

import com.assignment.contactmanager.dto.response.ContactResponseDto;
import com.assignment.contactmanager.dto.response.ErrorResponseDto;
import com.assignment.contactmanager.service.ContactService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contact")
@Slf4j
public class ContactController {

    @Autowired
    private ContactService contactService;

    @PostMapping("/add")
    public ResponseEntity<?> addContact(@RequestBody String addContactDto) throws JsonProcessingException {
        log.info("received request to add contact {}",addContactDto);
        try {
            return contactService.addContact(addContactDto);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Invalid request data."));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteContact(@RequestParam Long contactId) {
        log.info("received request to delete contact with contactId: {}",contactId);
        return contactService.deleteContact(contactId);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateContact(@RequestParam Long contactId ,@RequestBody String addUpdateContactDto) throws JsonProcessingException {
        log.info("received request to update contact with contactId {}",contactId);
        try {
            return contactService.updateContact(contactId, addUpdateContactDto);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Invalid request data."));
        }
    }
    @GetMapping("/read")
    public ResponseEntity<?> getContact(@RequestParam Long contactId) {
        log.info("received request to get contact with contactId {}",contactId);
        return contactService.getContact(contactId);
    }
    @GetMapping("/search")
    public ResponseEntity<?> searchContacts(@RequestBody String requestDto) throws JsonProcessingException{
        log.info("received request to search contacts  {}",requestDto);
        try {
            return contactService.searchContacts(requestDto);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Invalid request data."));
        }
    }

}
