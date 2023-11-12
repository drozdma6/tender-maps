package cz.cvut.fit.bap.parser.dao;

import cz.cvut.fit.bap.parser.domain.ContactPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContactPersonJpaRepository extends JpaRepository<ContactPerson, Long> {
    Optional<ContactPerson> findContactPersonByEmail(String email);
}
