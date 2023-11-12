package cz.cvut.fit.bap.parser.controller;


import cz.cvut.fit.bap.parser.business.ContactPersonService;
import cz.cvut.fit.bap.parser.domain.ContactPerson;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ContactPersonController extends AbstractController<ContactPersonService, ContactPerson, Long> {
    protected ContactPersonController(ContactPersonService service) {
        super(service);
    }

    @Override
    public ContactPerson save(ContactPerson contactPerson) {
        Optional<ContactPerson> contactPersonOptional = service.readByEmail(contactPerson.getEmail());
        return contactPersonOptional.orElseGet(() -> super.save(contactPerson));
    }
}
