package com.assignment.contactmanager.service.impl;

import com.assignment.contactmanager.dto.request.AddUpdateContactRequestDto;
import com.assignment.contactmanager.dto.request.SearchRequestDto;
import com.assignment.contactmanager.dto.response.ContactResponseDto;
import com.assignment.contactmanager.dto.response.ErrorResponseDto;
import com.assignment.contactmanager.dto.response.SearchResponseDto;
import com.assignment.contactmanager.dto.response.SuccessResponseDto;
import com.assignment.contactmanager.entity.Contact;
import com.assignment.contactmanager.enums.SearchType;
import com.assignment.contactmanager.repository.ContactRepository;
import com.assignment.contactmanager.service.ContactService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ContactServiceImpl implements ContactService {
    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ContactRepository contactRepository;

    @Override
    @Transactional
    public ResponseEntity<?> addContact(String requestDto) throws JsonProcessingException {
        AddUpdateContactRequestDto addUpdateContactRequestDto = objectMapper.readValue(requestDto, AddUpdateContactRequestDto.class);

        if (isNullOrEmpty(addUpdateContactRequestDto.getFirstName()) ||
                isNullOrEmpty(addUpdateContactRequestDto.getLastName()) ||
                isNullOrEmpty(addUpdateContactRequestDto.getPhoneNumber()) ||
                isNullOrEmpty(addUpdateContactRequestDto.getEmail())) {
            return ResponseEntity.badRequest().body(new ErrorResponseDto("All fields (FirstName, LastName, PhoneNumber, Email) are required."));
        }
        log.info("Adding new contact with email {}", addUpdateContactRequestDto.getEmail());

        if(contactRepository.findByEmail(addUpdateContactRequestDto.getEmail()).isPresent() || contactRepository.findByPhoneNumber(addUpdateContactRequestDto.getEmail()).isPresent()){
            log.error("PhoneNumber / Email Already exists");
            return ResponseEntity.badRequest().body(new ErrorResponseDto("PhoneNumber or Email Already exists"));
        }

        Contact contact = Contact.builder()
                .firstName(addUpdateContactRequestDto.getFirstName())
                .lastName(addUpdateContactRequestDto.getLastName())
                .email(addUpdateContactRequestDto.getEmail())
                .phoneNumber(addUpdateContactRequestDto.getPhoneNumber())
                .build();

        contactRepository.save(contact);
        return ResponseEntity.ok().body(new SuccessResponseDto("New contact saved with contactId : "+contact.getContactId()));
    }



    @Override
    @Transactional
    public ResponseEntity<?> deleteContact(Long contactId) {
        if(contactId == null){
            log.error("ContactId is required for deleting a contact.");
            return ResponseEntity.badRequest().body(new ErrorResponseDto("ContactId is required for deleting a contact."));
        }
        if(!contactRepository.existsById(contactId)){
            log.error("Contact with contactId: {} not found", contactId);
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Contact for id "+ contactId+" not found"));
        }
        log.info("Deleting contact with contactId: {}", contactId);
        contactRepository.deleteById(contactId);
        return ResponseEntity.badRequest().body(new SuccessResponseDto("Contact deleted with contactId: " + contactId));
    }

    @Override
    @Transactional
    public ResponseEntity<?> updateContact(Long contactId, String requestDto) throws JsonProcessingException {
        if (contactId == null) {
            return ResponseEntity.badRequest().body(new ErrorResponseDto("ContactId is required for updating a contact."));
        }
        Optional<Contact> existingContactInfo = contactRepository.findById(contactId);
        if(!existingContactInfo.isPresent()){
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Contact with contactId " + contactId + " not found."));
        }
        Contact existingContact = existingContactInfo.get();
        AddUpdateContactRequestDto addUpdateContactRequestDto = objectMapper.readValue(requestDto, AddUpdateContactRequestDto.class);
        String newEmail = addUpdateContactRequestDto.getEmail();
        String newPhoneNumber = addUpdateContactRequestDto.getPhoneNumber();
        String oldEmail = existingContact.getEmail();
        String oldPhoneNumber = existingContact.getPhoneNumber();

        if(!newEmail.equals(oldEmail)){
            if(contactRepository.findByEmail(newEmail).isPresent()){
                log.error("Email Already exists");
                return ResponseEntity.badRequest().body(new ErrorResponseDto("Email Already exists"));
            }
        }
        if(!newPhoneNumber.equals(oldPhoneNumber)){
            if(contactRepository.findByPhoneNumber(newPhoneNumber).isPresent()){
                log.error("PhoneNumber Already exists");
                return ResponseEntity.badRequest().body(new ErrorResponseDto("PhoneNumber Already exists"));
            }
        }

        existingContact.setFirstName(addUpdateContactRequestDto.getFirstName());
        existingContact.setLastName(addUpdateContactRequestDto.getLastName());
        existingContact.setEmail(addUpdateContactRequestDto.getEmail());
        existingContact.setPhoneNumber(addUpdateContactRequestDto.getPhoneNumber());

        contactRepository.save(existingContact);

        return ResponseEntity.ok().body(new SuccessResponseDto("Contact with contactId " + contactId + " updated successfully."));
    }

    @Override
    public ResponseEntity<?> getContact(Long contactId) {
        if(contactId == null){
            log.error("ContactId is required for get a contact.");
            return ResponseEntity.badRequest().body(new ErrorResponseDto("ContactId is required for getting a contact."));
        }
        Optional<Contact> contactInfo = contactRepository.findById(contactId);
        if(!contactInfo.isPresent()){
            log.error("Contact with contactId: {} not found", contactId);
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Contact for id "+ contactId+" not found"));
        }
        log.info("Getting contact with contactId: {}", contactId);
        Contact contact = contactInfo.get();
        ContactResponseDto response = toContactResponse(contact);
        return ResponseEntity.ok().body(response);
    }

    @Override
    public ResponseEntity<?> searchContacts(String requestDto) throws JsonProcessingException {
        SearchRequestDto searchRequestDto = objectMapper.readValue(requestDto, SearchRequestDto.class);
        String searchString  = searchRequestDto.getSearchString();
        log.info("Searching for {}", searchString);

        SearchType searchType = searchRequestDto.getSearchType();
        if(isNullOrEmpty(searchString)){
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Search string is required."));
        }
        List<Contact> searchResult = null;
        if(searchType == SearchType.EMAIL){
            Optional<Contact> result = contactRepository.findByEmail(searchString);
            if(result.isPresent()){
                SearchResponseDto response = new SearchResponseDto();
                ContactResponseDto contactResponse = toContactResponse(result.get());
                List<ContactResponseDto> list = new ArrayList<>();
                list.add(contactResponse);
                return ResponseEntity.ok().body(list);
            }
            else{
                return ResponseEntity.badRequest().body(new ErrorResponseDto("No item found"));
            }
        }
        else if(searchType == SearchType.FIRST_NAME){
            searchResult = contactRepository.findByFirstName(searchString);
        }
        else if(searchType == SearchType.LAST_NAME){
            searchResult = contactRepository.findByLastName(searchString);
        }
        if(searchResult != null){
            List<ContactResponseDto> list = new ArrayList<>();
            for(Contact contact : searchResult){
                list.add(toContactResponse(contact));
            }
            return ResponseEntity.ok().body(list);

        }
        else{
            return ResponseEntity.badRequest().body(new ErrorResponseDto("No item found"));
        }
    }

    private ContactResponseDto toContactResponse(Contact contact){
        return ContactResponseDto.builder()
                .contactId(contact.getContactId())
                .firstName(contact.getFirstName())
                .lastName(contact.getLastName())
                .email(contact.getEmail())
                .phoneNumber(contact.getPhoneNumber())
                .build();
    }
    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
