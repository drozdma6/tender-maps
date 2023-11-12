package cz.cvut.fit.bap.parser.controller;

import cz.cvut.fit.bap.parser.business.ContactPersonService;
import cz.cvut.fit.bap.parser.domain.ContactPerson;
import cz.cvut.fit.bap.parser.domain.ContractingAuthority;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactPersonControllerTest {
    @InjectMocks
    private ContactPersonController contactPersonController;

    @Mock
    private ContactPersonService contactPersonService;

    @Test
    void saveExisting() {
        ContractingAuthority contractingAuthority = new ContractingAuthority();
        ContactPerson contactPerson = new ContactPerson("name", "surname", "email", contractingAuthority);
        when(contactPersonService.readByEmail(contactPerson.getEmail())).thenReturn(Optional.of(contactPerson));

        ContactPerson savedContactPerson = contactPersonController.save(contactPerson);
        verify(contactPersonService, never()).create(any(ContactPerson.class));
        Assertions.assertEquals(contactPerson.getName(), savedContactPerson.getName());
        Assertions.assertEquals(contactPerson.getSurname(), savedContactPerson.getSurname());
        Assertions.assertEquals(contactPerson.getEmail(), savedContactPerson.getEmail());
    }

    @Test
    void save() {
        ContractingAuthority contractingAuthority = new ContractingAuthority();
        ContactPerson contactPerson = new ContactPerson("name", "surname", "email", contractingAuthority);
        when(contactPersonService.readByEmail(contactPerson.getEmail())).thenReturn(Optional.empty());

        ContactPerson savedContactPerson = contactPersonController.save(contactPerson);
        verify(contactPersonService).create(any(ContactPerson.class));
        Assertions.assertNull(savedContactPerson);
    }
}