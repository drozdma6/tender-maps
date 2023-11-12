package cz.cvut.fit.bap.parser.business;

import cz.cvut.fit.bap.parser.dao.ContactPersonJpaRepository;
import cz.cvut.fit.bap.parser.domain.ContactPerson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ContactPersonServiceTest {
    @InjectMocks
    private ContactPersonService contactPersonService;

    @Mock
    private ContactPersonJpaRepository contactPersonJpaRepository;

    @Test
    void readByEmailExisting() {
        String email = "email";
        ContactPerson contactPerson = new ContactPerson("name", "surname", email, null);
        when(contactPersonJpaRepository.findContactPersonByEmail(email)).thenReturn(Optional.of(contactPerson));
        Optional<ContactPerson> contactPersonOptional = contactPersonService.readByEmail(email);
        assertTrue(contactPersonOptional.isPresent());
        assertEquals(contactPerson, contactPersonOptional.get());
        assertEquals(contactPerson.getName(), contactPersonOptional.get().getName());
        assertEquals(contactPerson.getSurname(), contactPersonOptional.get().getSurname());
        assertEquals(contactPerson.getEmail(), contactPersonOptional.get().getEmail());
    }

    @Test
    void readByEmail() {
        String email = "email";
        when(contactPersonJpaRepository.findContactPersonByEmail(email)).thenReturn(Optional.empty());
        Optional<ContactPerson> contactPersonOptional = contactPersonService.readByEmail(email);
        assertTrue(contactPersonOptional.isEmpty());
    }
}
