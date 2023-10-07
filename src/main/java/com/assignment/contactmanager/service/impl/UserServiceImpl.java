package com.assignment.contactmanager.service.impl;

import com.assignment.contactmanager.dto.request.AddUserRequestDto;
import com.assignment.contactmanager.dto.response.ErrorResponseDto;
import com.assignment.contactmanager.dto.response.SuccessResponseDto;
import com.assignment.contactmanager.entity.User;
import com.assignment.contactmanager.repository.UserRepository;
import com.assignment.contactmanager.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserDetailsService, UserService {
    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ResponseEntity<?> addUser(String requestDto) throws JsonProcessingException {
        AddUserRequestDto addUserRequestDto = objectMapper.readValue(requestDto, AddUserRequestDto.class);
        log.info("Adding new user with email {}", addUserRequestDto.getEmail());

        String email = addUserRequestDto.getEmail();
        String password = addUserRequestDto.getPassword();

        if(isNullOrEmpty(email) || isNullOrEmpty(password)){
            return ResponseEntity.badRequest().body(new ErrorResponseDto("All fields (Email, Password) are required."));
        }

        String encryptedPassword = passwordEncoder.encode(password);


        User user = User.builder()
                .email(email)
                .password(encryptedPassword)
                .build();
        userRepository.save(user);
        return ResponseEntity.ok().body(new SuccessResponseDto("New user added with email: "+user.getEmail()));
    }

    @Override
    public ContactManagerUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);
        user.orElseThrow(() -> new UsernameNotFoundException("Not found: " + email));
        return user.map(ContactManagerUserDetails::new).get();
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

}
