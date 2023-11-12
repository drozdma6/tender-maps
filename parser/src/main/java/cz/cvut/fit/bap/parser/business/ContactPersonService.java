package cz.cvut.fit.bap.parser.business;

import cz.cvut.fit.bap.parser.dao.ContactPersonJpaRepository;
import cz.cvut.fit.bap.parser.domain.ContactPerson;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ContactPersonService extends AbstractCreateService<ContactPerson, Long> {
    protected ContactPersonService(ContactPersonJpaRepository repository) {
        super(repository);
    }

    /**
     * Reads contact person by email
     *
     * @param email of wanted contact person
     * @return optional of contact person
     */
    public Optional<ContactPerson> readByEmail(String email) {
        return ((ContactPersonJpaRepository) repository).findContactPersonByEmail(email);
    }
}
