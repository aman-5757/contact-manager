package com.assignment.contactmanager.controller;

import com.assignment.contactmanager.repository.UserRepository;
import com.assignment.contactmanager.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/add")
    public ResponseEntity<?> addUser(@RequestBody String addUserDto) throws JsonProcessingException {
        log.info("received request to add user {}",addUserDto);
        return userService.addUser(addUserDto);
    }


}
