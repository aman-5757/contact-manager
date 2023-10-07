package com.assignment.contactmanager.repository;

import com.assignment.contactmanager.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact,Long> {
    Optional<Contact> findByPhoneNumber(String phoneNumber);
    Optional<Contact> findByEmail(String email);
    List<Contact> findByFirstName(String firstName);
    List<Contact> findByLastName(String lastName);

}
