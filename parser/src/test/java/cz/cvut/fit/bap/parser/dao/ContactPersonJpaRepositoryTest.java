package cz.cvut.fit.bap.parser.dao;

import cz.cvut.fit.bap.parser.TestConfigurationClass;
import cz.cvut.fit.bap.parser.domain.ContactPerson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import(TestConfigurationClass.class)
public class ContactPersonJpaRepositoryTest {
    @Autowired
    private ContactPersonJpaRepository contactPersonJpaRepository;

    @Test
    public void findContactPersonByEmail() {
        String email = "email";
        ContactPerson contactPerson = new ContactPerson("name", "surname", email, null);

        Assertions.assertTrue(contactPersonJpaRepository.findContactPersonByEmail(email).isEmpty());

        contactPersonJpaRepository.save(contactPerson);

        Optional<ContactPerson> contactPersonOptional = contactPersonJpaRepository.findContactPersonByEmail(email);

        assertTrue(contactPersonOptional.isPresent());
        assertEquals(email, contactPersonOptional.get().getEmail());
        assertEquals(contactPerson.getName(), contactPersonOptional.get().getName());
        assertEquals(contactPerson.getSurname(), contactPersonOptional.get().getSurname());
    }
}
